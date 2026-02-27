package org.delcom.dao

import org.delcom.tables.PigTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class PigDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, PigDAO>(PigTable)

    var nama by PigTable.nama
    var pathGambar by PigTable.pathGambar
    var asalPerkembangan by PigTable.asalPerkembangan
    var ciriCiri by PigTable.ciriCiri
    var keunggulan by PigTable.keunggulan
    var createdAt by PigTable.createdAt
    var updatedAt by PigTable.updatedAt
}