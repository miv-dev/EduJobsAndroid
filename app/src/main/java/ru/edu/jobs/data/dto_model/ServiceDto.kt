package ru.edu.jobs.data.dto_model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: Int,
    @SerialName("service_name")
    val serviceName: String,
    @SerialName("service_details")
    val serviceDetails: String,
    @SerialName("date_created")
    val dateCreated: String?,
    val deadline: String?,
    val user: UserDto,
)


@Serializable
data class DepartmentDto(
    val id: Int,
    val name: String,
    val description: String,
    val website: String
)
