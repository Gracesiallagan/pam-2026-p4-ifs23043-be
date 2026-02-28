package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IDrinkRepository
import org.delcom.repositories.DrinkRepository
import org.delcom.services.PlantService
import org.delcom.services.DrinkService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {

    // Plant
    single<IPlantRepository> {
        PlantRepository()
    }

    single {
        PlantService(get())
    }

    // Drink
    single<IDrinkRepository> {
        DrinkRepository()
    }

    single {
        DrinkService(get())
    }

    // Profile
    single {
        ProfileService()
    }
}