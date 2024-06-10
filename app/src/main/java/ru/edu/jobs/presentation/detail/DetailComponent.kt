package ru.edu.jobs.presentation.detail

import kotlinx.coroutines.flow.StateFlow

interface DetailComponent {
    val model: StateFlow<DetailStore.State>

    fun onBackClicked()

    fun onFavoriteClicked(status: Boolean)

    fun onEditClicked()

}