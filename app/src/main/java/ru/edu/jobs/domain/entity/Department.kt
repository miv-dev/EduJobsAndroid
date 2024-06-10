package ru.edu.jobs.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Department(
    val id: Int,
    val name: String,
    val description: String,
    val website: String
)
