package ru.edu.jobs.data.dto_model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val tokens: TokenPair
)
