package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DrinkTable : UUIDTable("drinks") {
    val nama = varchar("nama", 100)
    val deskripsi = text("deskripsi")
    val bahanUtama = varchar("bahan_utama", 150)
    val caraPenyajian = text("cara_penyajian")
    val manfaat = text("manfaat")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}