package org.delcom.module

import org.delcom.repositories.IPigRepository
import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PigRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.PigService
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.koin.dsl.module


val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // Pig Repository
    single<IPigRepository> {
        PigRepository()
    }

    // Pig Service
    single {
        PigService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}