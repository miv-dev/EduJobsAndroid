package ru.edu.jobs.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val username: String,
    val role: Role?,
    val profile: Profile,
    val department: Department?
)

enum class Role {
    Specialist,
    Employer,
    Admin
}

@Serializable
data class Profile(
    val id: Int,
    val avatar: String?,
    val lastName: String?,
    val firstName: String?,
    val phone: String?,
)