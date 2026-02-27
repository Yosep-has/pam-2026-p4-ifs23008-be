package org.delcom.repositories

import org.delcom.dao.PigDAO
import org.delcom.entities.Pig
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.PigTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class PigRepository : IPigRepository {

    // Fungsi untuk mengambil semua data Pig dengan pencarian nama
    override suspend fun getPigs(search: String): List<Pig> = suspendTransaction {
        if (search.isBlank()) {
            // Mengambil data Pig jika pencarian kosong
            PigDAO.all()
                .orderBy(PigTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            // Mengambil data Pig berdasarkan pencarian nama
            PigDAO
                .find { PigTable.nama.lowerCase() like keyword }
                .orderBy(PigTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    // Fungsi untuk mengambil data Pig berdasarkan ID
    override suspend fun getPigById(id: String): Pig? = suspendTransaction {
        PigDAO
            .find { (PigTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    // Fungsi untuk mengambil data Pig berdasarkan nama
    override suspend fun getPigByName(name: String): Pig? = suspendTransaction {
        PigDAO
            .find { (PigTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    // Fungsi untuk menambahkan Pig baru ke dalam database
    override suspend fun addPig(pig: Pig): String = suspendTransaction {
        val pigDAO = PigDAO.new {
            nama = pig.nama
            pathGambar = pig.pathGambar
            asalPerkembangan = pig.asalPerkembangan
            ciriCiri = pig.ciriCiri
            keunggulan = pig.keunggulan
            createdAt = pig.createdAt
            updatedAt = pig.updatedAt
        }

        pigDAO.id.value.toString() // Mengembalikan ID Pig yang baru ditambahkan
    }

    // Fungsi untuk mengupdate data Pig berdasarkan ID
    override suspend fun updatePig(id: String, newPig: Pig): Boolean = suspendTransaction {
        val pigDAO = PigDAO
            .find { PigTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (pigDAO != null) {
            pigDAO.nama = newPig.nama
            pigDAO.pathGambar = newPig.pathGambar
            pigDAO.asalPerkembangan = newPig.asalPerkembangan
            pigDAO.ciriCiri = newPig.ciriCiri
            pigDAO.keunggulan = newPig.keunggulan
            pigDAO.updatedAt = newPig.updatedAt
            true
        } else {
            false
        }
    }

    // Fungsi untuk menghapus Pig berdasarkan ID
    override suspend fun removePig(id: String): Boolean = suspendTransaction {
        val rowsDeleted = PigTable.deleteWhere {
            PigTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1 // Mengembalikan true jika satu baris berhasil dihapus
    }
}