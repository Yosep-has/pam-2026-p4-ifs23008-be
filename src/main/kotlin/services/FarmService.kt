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
import org.delcom.data.FarmRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IFarmRepository
import java.io.File
import java.util.*

class FarmService(private val farmRepository: IFarmRepository) {

    // Mengambil semua data farm
    suspend fun getAllFarms(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val farms = farmRepository.getFarms(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar farm",
            mapOf(Pair("farms", farms))
        )
        call.respond(response)
    }

    // Mengambil data farm berdasarkan id
    suspend fun getFarmById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID farm tidak boleh kosong!")

        val farm = farmRepository.getFarmById(id) ?: throw AppException(404, "Data farm tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data farm",
            mapOf(Pair("farm", farm))
        )
        call.respond(response)
    }

    // Ambil data request
    private suspend fun getFarmRequest(call: ApplicationCall): FarmRequest {
        val farmReq = FarmRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> farmReq.nama = part.value.trim()
                        "deskripsi" -> farmReq.deskripsi = part.value
                        "jumlah" -> farmReq.jumlah = part.value.toIntOrNull() ?: 0
                        "pakan" -> farmReq.pakan = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/farms/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().toByteArray().let { bytes ->
                        file.writeBytes(bytes)
                    }
                    farmReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return farmReq
    }

    // Validasi request data dari pengguna
    private fun validateFarmRequest(farmReq: FarmRequest) {
        val validatorHelper = ValidatorHelper(farmReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("pakan", "Pakan tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(farmReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar farm gagal diupload!")
        }
    }

    // Menambahkan data farm
    suspend fun createFarm(call: ApplicationCall) {
        val farmReq = getFarmRequest(call)

        validateFarmRequest(farmReq)

        val existFarm = farmRepository.getFarmByName(farmReq.nama)
        if (existFarm != null) {
            val tmpFile = File(farmReq.pathGambar)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw AppException(409, "Farm dengan nama ini sudah terdaftar!")
        }

        val farmId = farmRepository.addFarm(farmReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data farm",
            mapOf(Pair("farmId", farmId))
        )
        call.respond(response)
    }

    // Mengubah data farm
    suspend fun updateFarm(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID farm tidak boleh kosong!")

        val oldFarm = farmRepository.getFarmById(id) ?: throw AppException(404, "Data farm tidak tersedia!")

        val farmReq = getFarmRequest(call)

        if (farmReq.pathGambar.isEmpty()) {
            farmReq.pathGambar = oldFarm.pathGambar
        }

        validateFarmRequest(farmReq)

        if (farmReq.nama != oldFarm.nama) {
            val existFarm = farmRepository.getFarmByName(farmReq.nama)
            if (existFarm != null) {
                val tmpFile = File(farmReq.pathGambar)
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }
                throw AppException(409, "Farm dengan nama ini sudah terdaftar!")
            }
        }

        if (farmReq.pathGambar != oldFarm.pathGambar) {
            val oldFile = File(oldFarm.pathGambar)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val isUpdated = farmRepository.updateFarm(id, farmReq.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data farm!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data farm",
            null
        )
        call.respond(response)
    }

    // Menghapus data farm
    suspend fun deleteFarm(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID farm tidak boleh kosong!")

        val oldFarm = farmRepository.getFarmById(id) ?: throw AppException(404, "Data farm tidak tersedia!")

        val oldFile = File(oldFarm.pathGambar)

        val isDeleted = farmRepository.removeFarm(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data farm!")
        }

        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data farm",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar farm
    suspend fun getFarmImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val farm = farmRepository.getFarmById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(farm.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}
