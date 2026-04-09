package org.delcom.dao

import org.delcom.tables.FarmTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

class FarmDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, FarmDAO>(FarmTable)

    var nama by FarmTable.nama
    var pathGambar by FarmTable.pathGambar
    var deskripsi by FarmTable.deskripsi
    var jumlah by FarmTable.jumlah
    var pakan by FarmTable.pakan
    var createdAt by FarmTable.createdAt
    var updatedAt by FarmTable.updatedAt
}
