package ru.edu.jobs.domain.repository

import ru.edu.jobs.domain.entity.Service

interface ServiceRepository {

    suspend fun getServiceById(id: Int): Service


    fun cacheServices(services: List<Service>)
    suspend fun searchServices(query: String): List<Service>
}