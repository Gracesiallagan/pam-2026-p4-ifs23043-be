package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DrinkRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDrinkRepository

class DrinkService(private val drinkRepository: IDrinkRepository) {

    // Mengambil semua data minuman
    suspend fun getAllDrinks(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val drinks = drinkRepository.getDrinks(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar minuman",
            mapOf("drinks" to drinks)
        )
        call.respond(response)
    }

    // Mengambil data minuman berdasarkan id
    suspend fun getDrinkById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID minuman tidak boleh kosong!")

        val drink = drinkRepository.getDrinkById(id)
            ?: throw AppException(404, "Data minuman tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data minuman",
            mapOf("drink" to drink)
        )
        call.respond(response)
    }

    // Validasi request data minuman
    private fun validateDrinkRequest(drinkReq: DrinkRequest) {
        val validatorHelper = ValidatorHelper(drinkReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("bahanUtama", "Bahan utama tidak boleh kosong")
        validatorHelper.required("caraPenyajian", "Cara penyajian tidak boleh kosong")
        validatorHelper.required("manfaat", "Manfaat tidak boleh kosong")
        validatorHelper.validate()
    }

    // Menambahkan data minuman
    suspend fun createDrink(call: ApplicationCall) {
        val drinkReq = call.receive<DrinkRequest>()

        validateDrinkRequest(drinkReq)

        // Cek nama minuman duplikat
        val existDrink = drinkRepository.getDrinkByName(drinkReq.nama)
        if (existDrink != null) {
            throw AppException(409, "Minuman dengan nama ini sudah terdaftar!")
        }

        val drinkId = drinkRepository.addDrink(
            drinkReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data minuman",
            mapOf("drinkId" to drinkId)
        )
        call.respond(response)
    }

    // Mengubah data minuman
    suspend fun updateDrink(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID minuman tidak boleh kosong!")

        val oldDrink = drinkRepository.getDrinkById(id)
            ?: throw AppException(404, "Data minuman tidak tersedia!")

        val drinkReq = call.receive<DrinkRequest>()

        validateDrinkRequest(drinkReq)

        // Cek nama jika diubah
        if (drinkReq.nama != oldDrink.nama) {
            val existDrink = drinkRepository.getDrinkByName(drinkReq.nama)
            if (existDrink != null) {
                throw AppException(409, "Minuman dengan nama ini sudah terdaftar!")
            }
        }

        val isUpdated = drinkRepository.updateDrink(
            id,
            drinkReq.toEntity()
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data minuman!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data minuman",
            null
        )
        call.respond(response)
    }

    // Menghapus data minuman
    suspend fun deleteDrink(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID minuman tidak boleh kosong!")

        val oldDrink = drinkRepository.getDrinkById(id)
            ?: throw AppException(404, "Data minuman tidak tersedia!")

        val isDeleted = drinkRepository.removeDrink(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data minuman!")
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data minuman",
            null
        )
        call.respond(response)
    }
}