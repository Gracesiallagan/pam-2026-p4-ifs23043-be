package org.delcom.repositories

import org.delcom.entities.Drink

interface IDrinkRepository {
    suspend fun getDrinks(search: String): List<Drink>
    suspend fun getDrinkById(id: String): Drink?
    suspend fun getDrinkByName(name: String): Drink?
    suspend fun addDrink(drink: Drink): String
    suspend fun updateDrink(id: String, newDrink: Drink): Boolean
    suspend fun removeDrink(id: String): Boolean
}