package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Pig

@Serializable
data class PigRequest(
    var nama: String = "",
    var asalPerkembangan: String = "",
    var ciriCiri: String = "",
    var keunggulan: String = "",
    var pathGambar: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "asalPerkembangan" to asalPerkembangan,
            "ciriCiri" to ciriCiri,
            "keunggulan" to keunggulan,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Pig {
        return Pig(
            nama = nama,
            asalPerkembangan = asalPerkembangan,
            ciriCiri = ciriCiri,
            keunggulan = keunggulan,
            pathGambar = pathGambar
        )
    }
}