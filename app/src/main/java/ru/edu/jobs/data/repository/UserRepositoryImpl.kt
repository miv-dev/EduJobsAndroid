package ru.edu.jobs.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.local.UserService
import ru.edu.jobs.data.mappers.toUser
import ru.edu.jobs.data.mappers.toUserDto
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.repository.UserRepository
import ru.edu.jobs.ext.mergeWith
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userService: UserService,
    private val errorService: ErrorService
) : UserRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var _user: User? = null

    private val user: User?
        get() = _user


    private val _userChangedFlow = MutableSharedFlow<Boolean>()
    override val userChangedFlow: Flow<Boolean> = _userChangedFlow.stateIn(
        scope = scope, started = SharingStarted.Eagerly, initialValue = false
    )


    private val _updateEventFlow = MutableSharedFlow<Unit>(replay = 1)
    private val refreshedUserFlow = MutableSharedFlow<User?>()

    private val cachedUserFlow = flow {
        _updateEventFlow.emit(Unit)
        _updateEventFlow.collect {
            runCatching {
                apiService.getCurrentUser()
            }.onSuccess {
                val id = userService.loadLastLoggedUserId()
                _user = it.toUser()

                if (id != it.id) {
                    userService.setLastLoggedUserId(it.id)
                    _userChangedFlow.emit(true)
                }
                emit(user)
            }.onFailure {
                _user = null
                emit(user)
            }
        }
    }.retry(3)

    override val userFlow: Flow<User?> = cachedUserFlow.mergeWith(refreshedUserFlow).stateIn(
        scope = scope, started = SharingStarted.Eagerly, initialValue = user
    )

    override fun refreshUser() {
        scope.launch {
            _updateEventFlow.emit(Unit)
        }
    }

    override fun refreshUser(user: User?) {
        scope.launch {
            _user = user
            val id = userService.loadLastLoggedUserId()
            _userChangedFlow.emit(false)


//            if (id != (user?.id ?: -1)) { // TODO
            userService.setLastLoggedUserId(user?.id ?: -1)
            _userChangedFlow.emit(true)
//            }
            refreshedUserFlow.emit(user)
        }
    }

    override suspend fun updateUser(user: User): Response<Unit> {
        try {

            val response = apiService.updateUser(user.toUserDto())
            if (response.status.value in 200..299) {
                if (user.role != _user?.role || user.department != _user?.department) {
                    scope.launch {
                        _userChangedFlow.emit(true)
                    }
                }
                refreshUser(user)
                return Response.Success(Unit)
            } else {
                errorService.getError(response).let {
                    return Response.Error(it)
                }
            }
        } catch (e: Exception) {
            errorService.getError(e).let {
                return Response.Error(it)
            }
        }
    }
}

