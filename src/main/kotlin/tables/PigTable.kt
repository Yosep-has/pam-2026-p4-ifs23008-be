package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object PigTable : UUIDTable("pigs") {  // Tabel untuk babi
    val nama = varchar("nama", 100)         // Nama babi
    val pathGambar = varchar("path_gambar", 255) // Path gambar babi
    val asalPerkembangan = text("asal_perkembangan") // Asal dan perkembangan babi
    val ciriCiri = text("ciri_ciri")        // Ciri-ciri babi
    val keunggulan = text("keunggulan")      // Keunggulan babi
    val createdAt = timestamp("created_at") // Timestamp saat babi dibuat
    val updatedAt = timestamp("updated_at") // Timestamp saat babi terakhir diupdate
}