package org.delcom.repositories

import org.delcom.dao.FarmDAO
import org.delcom.entities.Farm
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.FarmTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class FarmRepository : IFarmRepository {

    // Fungsi untuk mengambil semua data Farm dengan pencarian nama
    override suspend fun getFarms(search: String): List<Farm> = suspendTransaction {
        if (search.isBlank()) {
            FarmDAO.all()
                .orderBy(FarmTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            FarmDAO
                .find { FarmTable.nama.lowerCase() like keyword }
                .orderBy(FarmTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    // Fungsi untuk mengambil data Farm berdasarkan ID
    override suspend fun getFarmById(id: String): Farm? = suspendTransaction {
        FarmDAO
            .find { (FarmTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    // Fungsi untuk mengambil data Farm berdasarkan nama
    override suspend fun getFarmByName(name: String): Farm? = suspendTransaction {
        FarmDAO
            .find { (FarmTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    // Fungsi untuk menambahkan Farm baru ke dalam database
    override suspend fun addFarm(farm: Farm): String = suspendTransaction {
        val farmDAO = FarmDAO.new {
            nama = farm.nama
            pathGambar = farm.pathGambar
            deskripsi = farm.deskripsi
            jumlah = farm.jumlah
            pakan = farm.pakan
            createdAt = farm.createdAt
            updatedAt = farm.updatedAt
        }

        farmDAO.id.value.toString()
    }

    // Fungsi untuk mengupdate data Farm berdasarkan ID
    override suspend fun updateFarm(id: String, newFarm: Farm): Boolean = suspendTransaction {
        val farmDAO = FarmDAO
            .find { FarmTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (farmDAO != null) {
            farmDAO.nama = newFarm.nama
            farmDAO.pathGambar = newFarm.pathGambar
            farmDAO.deskripsi = newFarm.deskripsi
            farmDAO.jumlah = newFarm.jumlah
            farmDAO.pakan = newFarm.pakan
            farmDAO.updatedAt = newFarm.updatedAt
            true
        } else {
            false
        }
    }

    // Fungsi untuk menghapus Farm berdasarkan ID
    override suspend fun removeFarm(id: String): Boolean = suspendTransaction {
        val rowsDeleted = FarmTable.deleteWhere {
            FarmTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}
