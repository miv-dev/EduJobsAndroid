package ru.edu.jobs.data.dto_model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedServiceDto(
    val title: String,
    val description: String,
    val price: String?,
    @SerialName("site_name")
    val site: String,
    val url: String
)
