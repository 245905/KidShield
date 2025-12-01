package com.dominik.control.kidshield.data.model.dto

import java.util.UUID

data class GenerateCodeResponse(
    val id: UUID,
    val pin: String
)

data class PairByUUIDRequest(
    val id: UUID,
)

data class PairByPinRequest(
    val pin: String
)
