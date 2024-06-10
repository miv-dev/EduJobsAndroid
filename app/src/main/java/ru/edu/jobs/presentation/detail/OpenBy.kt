package ru.edu.jobs.presentation.detail

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import java.util.UUID


sealed class OpenBy : Parcelable {
    @Parcelize
    data class Viewer(val id: Int) : OpenBy()

    @Parcelize
    data class Owner(val id: Int) : OpenBy()

    @Parcelize
    data class Parsed(val uuid: UUID) : OpenBy()
}