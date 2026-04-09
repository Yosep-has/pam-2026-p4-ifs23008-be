package org.delcom.repositories

import org.delcom.entities.Farm

interface IFarmRepository {
    // Mendapatkan semua data Farm berdasarkan nama
    suspend fun getFarms(search: String): List<Farm>

    // Mendapatkan Farm berdasarkan ID
    suspend fun getFarmById(id: String): Farm?

    // Mendapatkan Farm berdasarkan nama
    suspend fun getFarmByName(name: String): Farm?

    // Menambahkan Farm baru
    suspend fun addFarm(farm: Farm): String

    // Mengupdate data Farm
    suspend fun updateFarm(id: String, newFarm: Farm): Boolean

    // Menghapus Farm berdasarkan ID
    suspend fun removeFarm(id: String): Boolean
}
