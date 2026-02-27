package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PigDAO
import org.delcom.dao.PlantDAO
import org.delcom.entities.Pig
import org.delcom.entities.Plant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

// Fungsi untuk mengonversi dari PlantDAO ke Plant Model
fun daoToModel(dao: PlantDAO) = Plant(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    manfaat = dao.manfaat,
    efekSamping = dao.efekSamping,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

// Fungsi untuk mengonversi dari PigDAO ke Pig Model
fun daoToModel(dao: PigDAO) = Pig(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    asalPerkembangan = dao.asalPerkembangan,
    ciriCiri = dao.ciriCiri,
    keunggulan = dao.keunggulan,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)