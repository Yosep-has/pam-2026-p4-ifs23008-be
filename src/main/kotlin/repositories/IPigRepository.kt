package org.delcom.repositories

import org.delcom.entities.Pig

interface IPigRepository {
    // Mendapatkan semua data Pig berdasarkan nama
    suspend fun getPigs(search: String): List<Pig>

    // Mendapatkan Pig berdasarkan ID
    suspend fun getPigById(id: String): Pig?

    // Mendapatkan Pig berdasarkan nama
    suspend fun getPigByName(name: String): Pig?

    // Menambahkan Pig baru
    suspend fun addPig(pig: Pig): String

    // Mengupdate data Pig
    suspend fun updatePig(id: String, newPig: Pig): Boolean

    // Menghapus Pig berdasarkan ID
    suspend fun removePig(id: String): Boolean
}