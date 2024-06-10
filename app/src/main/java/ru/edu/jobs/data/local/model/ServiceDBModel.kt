package ru.edu.jobs.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fav_services")
data class ServiceDBModel(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
)
