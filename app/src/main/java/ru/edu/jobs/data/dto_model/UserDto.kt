package ru.edu.jobs.data.dto_model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    @SerialName("user_type")
    val userType: String?,
    val department: DepartmentDto?,
    val profile: ProfileDto?
)

@Serializable
data class ProfileDto(
    val id: Int,
    val avatar: String?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("phone_number")
    val phoneNumber: String?
)