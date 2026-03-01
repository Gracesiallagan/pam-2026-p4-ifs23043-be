package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.PlantService
import org.delcom.services.DrinkService
import org.delcom.services.ProfileService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val plantService: PlantService by inject()
    val drinkService: DrinkService by inject()
    val profileService: ProfileService by inject()

    install(StatusPages) {

        // ======================
        // AppException (VALIDASI)
        // ======================
        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty())
                        cause.message
                    else
                        "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty())
                        null
                    else
                        dataMap.toString() // ✅ FIX FINAL
                )
            )
        }

        // ======================
        // ERROR LAIN
        // ======================
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = null
                )
            )
        }
    }

    routing {

        get("/") {
            call.respondText("API telah berjalan.oleh Grace Siallagan (JK)")
        }

        // ===== PLANTS =====
        route("/plants") {
            get { plantService.getAllPlants(call) }
            post { plantService.createPlant(call) }
            get("/{id}") { plantService.getPlantById(call) }
            put("/{id}") { plantService.updatePlant(call) }
            delete("/{id}") { plantService.deletePlant(call) }
            get("/{id}/image") { plantService.getPlantImage(call) }
        }

        // ===== DRINKS =====
        route("/drinks") {
            get { drinkService.getAllDrinks(call) }
            post { drinkService.createDrink(call) }
            get("/{id}") { drinkService.getDrinkById(call) }
            put("/{id}") { drinkService.updateDrink(call) }
            delete("/{id}") { drinkService.deleteDrink(call) }
        }

        // ===== PROFILE =====
        route("/profile") {
            get { profileService.getProfile(call) }
            get("/photo") { profileService.getProfilePhoto(call) }
        }
    }
}