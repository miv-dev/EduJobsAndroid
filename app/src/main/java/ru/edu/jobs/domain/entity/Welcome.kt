package ru.edu.jobs.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Welcome(
    @SerialName("light_image")
    val lightImage: String,
    @SerialName("dark_image")
    val darkImage: String,
    val title: String,
    val link: String
)
