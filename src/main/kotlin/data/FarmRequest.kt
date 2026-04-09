package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Farm

@Serializable
data class FarmRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var jumlah: Int = 0,
    var pakan: String = "",
    var pathGambar: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "jumlah" to jumlah,
            "pakan" to pakan,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Farm {
        return Farm(
            nama = nama,
            deskripsi = deskripsi,
            jumlah = jumlah,
            pakan = pakan,
            pathGambar = pathGambar
        )
    }
}
