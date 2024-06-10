package ru.edu.jobs.presentation.add_service

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize


sealed class OpenReason: Parcelable {

    @Parcelize
    data object AddService : OpenReason()

    @Parcelize
    data class EditService(val id: Int) : OpenReason()

}