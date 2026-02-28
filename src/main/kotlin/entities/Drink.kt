package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Drink(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var deskripsi: String,
    var bahanUtama: String,
    var caraPenyajian: String,
    var manfaat: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)