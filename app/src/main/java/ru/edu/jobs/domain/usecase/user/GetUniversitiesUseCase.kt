package ru.edu.jobs.domain.usecase.user

import ru.edu.jobs.domain.repository.UniversityRepository
import javax.inject.Inject

class GetUniversitiesUseCase @Inject constructor(
    private val repository: UniversityRepository
) {
    suspend operator fun invoke() = repository.loadUniversities()
}
