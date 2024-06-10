package ru.edu.jobs.domain.entity

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated: AuthState()
    data object Loading : AuthState()
}