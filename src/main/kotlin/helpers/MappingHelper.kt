package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.FarmDAO
import org.delcom.dao.PlantDAO
import org.delcom.entities.Farm
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

// Fungsi untuk mengonversi dari FarmDAO ke Farm Model
fun daoToModel(dao: FarmDAO) = Farm(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    jumlah = dao.jumlah,
    pakan = dao.pakan,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)
