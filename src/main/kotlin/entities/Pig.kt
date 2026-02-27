package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Pig(
    var id: String = UUID.randomUUID().toString(),  // ID unik untuk babi
    var nama: String,                              // Nama babi
    var pathGambar: String,                        // Path gambar babi
    var asalPerkembangan: String,                  // Asal dan perkembangan babi
    var ciriCiri: String,                          // Ciri-ciri babi
    var keunggulan: String,                        // Keunggulan babi

    @Contextual
    val createdAt: Instant = Clock.System.now(),   // Waktu pembuatan
    @Contextual
    var updatedAt: Instant = Clock.System.now(),   // Waktu pembaruan terakhir
)