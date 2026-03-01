package org.delcom.module

import org.delcom.repositories.IDrinkRepository
import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.DrinkRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.DrinkService
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository (TIDAK DIUBAH)
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service (TIDAK DIUBAH)
    single {
        PlantService(get())
    }

    // Profile Service (TIDAK DIUBAH)
    single {
        ProfileService()
    }

    // Drink Repository
    single<IDrinkRepository> {
        DrinkRepository()
    }

    // Drink Service
    single {
        DrinkService(get())
    }
}