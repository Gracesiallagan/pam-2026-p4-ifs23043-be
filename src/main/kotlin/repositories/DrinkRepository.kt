package org.delcom.repositories

import org.delcom.dao.DrinkDAO
import org.delcom.entities.Drink
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DrinkTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class DrinkRepository : IDrinkRepository {

    override suspend fun getDrinks(search: String): List<Drink> = suspendTransaction {
        if (search.isBlank()) {
            DrinkDAO.all()
                .orderBy(DrinkTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            DrinkDAO
                .find {
                    DrinkTable.nama.lowerCase() like keyword
                }
                .orderBy(DrinkTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getDrinkById(id: String): Drink? = suspendTransaction {
        DrinkDAO
            .find { (DrinkTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getDrinkByName(name: String): Drink? = suspendTransaction {
        DrinkDAO
            .find { (DrinkTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addDrink(drink: Drink): String = suspendTransaction {
        val drinkDAO = DrinkDAO.new {
            nama = drink.nama
            deskripsi = drink.deskripsi
            bahanUtama = drink.bahanUtama
            caraPenyajian = drink.caraPenyajian
            manfaat = drink.manfaat
            createdAt = drink.createdAt
            updatedAt = drink.updatedAt
        }

        drinkDAO.id.value.toString()
    }

    override suspend fun updateDrink(id: String, newDrink: Drink): Boolean = suspendTransaction {
        val drinkDAO = DrinkDAO
            .find { DrinkTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (drinkDAO != null) {
            drinkDAO.nama = newDrink.nama
            drinkDAO.deskripsi = newDrink.deskripsi
            drinkDAO.bahanUtama = newDrink.bahanUtama
            drinkDAO.caraPenyajian = newDrink.caraPenyajian
            drinkDAO.manfaat = newDrink.manfaat
            drinkDAO.updatedAt = newDrink.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeDrink(id: String): Boolean = suspendTransaction {
        val rowsDeleted = DrinkTable.deleteWhere {
            DrinkTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}