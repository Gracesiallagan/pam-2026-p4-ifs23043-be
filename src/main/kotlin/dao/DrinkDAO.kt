package org.delcom.dao

import org.delcom.tables.DrinkTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class DrinkDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, DrinkDAO>(DrinkTable)

    var nama by DrinkTable.nama
    var deskripsi by DrinkTable.deskripsi
    var bahanUtama by DrinkTable.bahanUtama
    var caraPenyajian by DrinkTable.caraPenyajian
    var manfaat by DrinkTable.manfaat
    var pathGambar by DrinkTable.pathGambar   // ✅ ditambahkan
    var createdAt by DrinkTable.createdAt
    var updatedAt by DrinkTable.updatedAt
}