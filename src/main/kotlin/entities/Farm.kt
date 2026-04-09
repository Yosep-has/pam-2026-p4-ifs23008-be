package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Farm(
    var id: String = UUID.randomUUID().toString(),  // ID unik untuk farm
    var nama: String,                               // Nama hewan ternak
    var pathGambar: String,                         // Path gambar hewan
    var deskripsi: String,                          // Deskripsi hewan ternak
    var jumlah: Int,                                // Jumlah populasi
    var pakan: String,                              // Jenis pakan

    @Contextual
    val createdAt: Instant = Clock.System.now(),    // Waktu pembuatan
    @Contextual
    var updatedAt: Instant = Clock.System.now(),    // Waktu pembaruan terakhir
)
