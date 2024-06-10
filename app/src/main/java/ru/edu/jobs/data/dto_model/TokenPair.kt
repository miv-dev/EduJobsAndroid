package ru.edu.jobs.data.dto_model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(
    @SerialName("access")
    val accessToken: String,
    @SerialName("refresh")
    val refreshToken: String
)
