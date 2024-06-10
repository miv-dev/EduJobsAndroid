package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.User

interface UserRepository {

    val userFlow: Flow<User?>

    fun refreshUser()
    fun refreshUser(user: User?)

    suspend fun updateUser(user: User): Response<Unit>
    val userChangedFlow: Flow<Boolean>
}

