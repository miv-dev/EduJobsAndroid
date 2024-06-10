package ru.edu.jobs.domain.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import java.time.LocalDate


data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val dateCreated: String?,
    val deadline: LocalDate?,
    val user: User
    )

fun Service.toShort() = ServiceShort(id, name, description)

@Parcelize
data class ServiceShort(
    val id: Int,
    val name: String,
    val description: String
): Parcelable

