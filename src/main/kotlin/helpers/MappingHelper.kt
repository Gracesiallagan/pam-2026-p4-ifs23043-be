package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PlantDAO
import org.delcom.dao.DrinkDAO
import org.delcom.entities.Plant
import org.delcom.entities.Drink
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// ===== Transaction Helper (UMUM) =====
suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)


// ===== Plant Mapper =====
fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)


// ===== Drink Mapper =====
fun daoToModel(dao: DrinkDAO) = Drink(
    dao.id.value.toString(),
    dao.nama,
    dao.deskripsi,
    dao.bahanUtama,
    dao.caraPenyajian,
    dao.manfaat,
    dao.createdAt,
    dao.updatedAt
)