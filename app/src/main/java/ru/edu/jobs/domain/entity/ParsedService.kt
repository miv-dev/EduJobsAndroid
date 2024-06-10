package ru.edu.jobs.domain.entity

import java.util.UUID

data class ParsedService(
    val uuid: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val price: String?,
    val site: Site,
    val url: String
)

enum class Site {
    Habr, Freelance, Other
}