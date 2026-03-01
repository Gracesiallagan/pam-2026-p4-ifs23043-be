package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DrinkRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDrinkRepository
import java.io.File
import java.util.UUID

class DrinkService(private val drinkRepository: IDrinkRepository) {

    // Mengambil semua data minuman
    suspend fun getAllDrinks(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val drinks = drinkRepository.getDrinks(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar minuman",
            mapOf(Pair("drinks", drinks))
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
            mapOf(Pair("drink", drink))
        )
        call.respond(response)
    }

    // Ambil data request dari multipart
    private suspend fun getDrinkRequest(call: ApplicationCall): DrinkRequest {
        val drinkReq = DrinkRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> drinkReq.nama = part.value.trim()
                        "deskripsi" -> drinkReq.deskripsi = part.value
                        "bahanUtama" -> drinkReq.bahanUtama = part.value.trim()
                        "caraPenyajian" -> drinkReq.caraPenyajian = part.value.trim()
                        "manfaat" -> drinkReq.manfaat = part.value.trim()
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/drinks/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    drinkReq.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return drinkReq
    }

    // Validasi request
    private fun validateDrinkRequest(drinkReq: DrinkRequest) {
        val validatorHelper = ValidatorHelper(drinkReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("bahanUtama", "Bahan utama tidak boleh kosong")
        validatorHelper.required("caraPenyajian", "Cara penyajian tidak boleh kosong")
        validatorHelper.required("manfaat", "Manfaat tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(drinkReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar minuman gagal diupload!")
        }
    }

    // Menambahkan data minuman
    suspend fun createDrink(call: ApplicationCall) {
        val drinkReq = getDrinkRequest(call)

        validateDrinkRequest(drinkReq)

        val existDrink = drinkRepository.getDrinkByName(drinkReq.nama)
        if (existDrink != null) {
            val tmpFile = File(drinkReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Minuman dengan nama ini sudah terdaftar!")
        }

        val drinkId = drinkRepository.addDrink(drinkReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data minuman",
            mapOf(Pair("drinkId", drinkId))
        )
        call.respond(response)
    }

    // Mengubah data minuman
    suspend fun updateDrink(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID minuman tidak boleh kosong!")

        val oldDrink = drinkRepository.getDrinkById(id)
            ?: throw AppException(404, "Data minuman tidak tersedia!")

        val drinkReq = getDrinkRequest(call)

        if (drinkReq.pathGambar.isEmpty()) {
            drinkReq.pathGambar = oldDrink.pathGambar
        }

        validateDrinkRequest(drinkReq)

        if (drinkReq.nama != oldDrink.nama) {
            val existDrink = drinkRepository.getDrinkByName(drinkReq.nama)
            if (existDrink != null) {
                val tmpFile = File(drinkReq.pathGambar)
                if (tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Minuman dengan nama ini sudah terdaftar!")
            }
        }

        if (drinkReq.pathGambar != oldDrink.pathGambar) {
            val oldFile = File(oldDrink.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = drinkRepository.updateDrink(id, drinkReq.toEntity())
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

        val oldFile = File(oldDrink.pathGambar)

        val isDeleted = drinkRepository.removeDrink(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data minuman!")
        }

        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse(
            "success",
            "Berhasil menghapus data minuman",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar minuman
    suspend fun getDrinkImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val drink = drinkRepository.getDrinkById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(drink.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}