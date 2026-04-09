package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object FarmTable : UUIDTable("farm") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("Deskripsi")
    val jumlah = integer("Populasi")
    val pakan = text("Pakan")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}