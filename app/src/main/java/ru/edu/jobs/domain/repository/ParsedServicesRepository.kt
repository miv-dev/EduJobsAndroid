package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.ParsedService
import java.util.UUID

interface ParsedServicesRepository {
    fun getParsedServiceById(uuid: UUID): ParsedService
    val servicesFlow: Flow<LoadState<List<ParsedService>>>
}