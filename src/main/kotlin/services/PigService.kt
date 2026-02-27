package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.PigRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IPigRepository
import java.io.File
import java.util.*

class PigService(private val pigRepository: IPigRepository) {

    // Mengambil semua data pig
    suspend fun getAllPigs(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val pigs = pigRepository.getPigs(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar pig",
            mapOf(Pair("pigs", pigs))
        )
        call.respond(response)
    }

    // Mengambil data pig berdasarkan id
    suspend fun getPigById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pig tidak boleh kosong!")

        val pig = pigRepository.getPigById(id) ?: throw AppException(404, "Data pig tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data pig",
            mapOf(Pair("pig", pig))
        )
        call.respond(response)
    }

    // Ambil data request
    private suspend fun getPigRequest(call: ApplicationCall): PigRequest {
        // Buat object penampung
        val pigReq = PigRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                // Ambil request berupa teks
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> pigReq.nama = part.value.trim()
                        "asalPerkembangan" -> pigReq.asalPerkembangan = part.value
                        "ciriCiri" -> pigReq.ciriCiri = part.value
                        "keunggulan" -> pigReq.keunggulan = part.value
                    }
                }

                // Upload file
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/pigs/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs() // pastikan folder ada

                    part.provider().toByteArray().let { bytes ->
                        file.writeBytes(bytes)
                    }
                    pigReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return pigReq
    }

    // Validasi request data dari pengguna
    private fun validatePigRequest(pigReq: PigRequest) {
        val validatorHelper = ValidatorHelper(pigReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("asalPerkembangan", "Asal perkembangan tidak boleh kosong")
        validatorHelper.required("ciriCiri", "Ciri-ciri tidak boleh kosong")
        validatorHelper.required("keunggulan", "Keunggulan tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(pigReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar pig gagal diupload!")
        }
    }

    // Menambahkan data pig
    suspend fun createPig(call: ApplicationCall) {
        // Ambil data request
        val pigReq = getPigRequest(call)

        // Validasi request
        validatePigRequest(pigReq)

        // Periksa pig dengan nama yang sama
        val existPig = pigRepository.getPigByName(pigReq.nama)
        if (existPig != null) {
            val tmpFile = File(pigReq.pathGambar)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw AppException(409, "Pig dengan nama ini sudah terdaftar!")
        }

        val pigId = pigRepository.addPig(pigReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data pig",
            mapOf(Pair("pigId", pigId))
        )
        call.respond(response)
    }

    // Mengubah data pig
    suspend fun updatePig(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pig tidak boleh kosong!")

        val oldPig = pigRepository.getPigById(id) ?: throw AppException(404, "Data pig tidak tersedia!")

        // Ambil data request
        val pigReq = getPigRequest(call)

        if (pigReq.pathGambar.isEmpty()) {
            pigReq.pathGambar = oldPig.pathGambar
        }

        // Validasi request
        validatePigRequest(pigReq)

        // Periksa pig dengan nama yang sama jika nama diubah
        if (pigReq.nama != oldPig.nama) {
            val existPig = pigRepository.getPigByName(pigReq.nama)
            if (existPig != null) {
                val tmpFile = File(pigReq.pathGambar)
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }
                throw AppException(409, "Pig dengan nama ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika mengupload file baru
        if (pigReq.pathGambar != oldPig.pathGambar) {
            val oldFile = File(oldPig.pathGambar)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val isUpdated = pigRepository.updatePig(id, pigReq.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data pig!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data pig",
            null
        )
        call.respond(response)
    }

    // Menghapus data pig
    suspend fun deletePig(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID pig tidak boleh kosong!")

        val oldPig = pigRepository.getPigById(id) ?: throw AppException(404, "Data pig tidak tersedia!")

        val oldFile = File(oldPig.pathGambar)

        val isDeleted = pigRepository.removePig(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data pig!")
        }

        // Hapus data gambar jika data pig sudah dihapus
        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data pig",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar pig
    suspend fun getPigImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val pig = pigRepository.getPigById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(pig.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}