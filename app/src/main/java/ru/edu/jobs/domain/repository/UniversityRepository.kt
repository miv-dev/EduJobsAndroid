package ru.edu.jobs.domain.repository

import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Department

interface UniversityRepository {

    suspend fun loadUniversities(): Response<List<Department>>

}