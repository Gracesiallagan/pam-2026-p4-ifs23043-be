package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Drink

@Serializable
data class DrinkRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var bahanUtama: String = "",
    var caraPenyajian: String = "",
    var manfaat: String = "",
    var pathGambar: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "bahanUtama" to bahanUtama,
            "caraPenyajian" to caraPenyajian,
            "manfaat" to manfaat,
            "pathGambar" to pathGambar,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
        )
    }

    fun toEntity(): Drink {
        return Drink(
            nama = nama,
            deskripsi = deskripsi,
            bahanUtama = bahanUtama,
            caraPenyajian = caraPenyajian,
            manfaat = manfaat,
            pathGambar = pathGambar,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}