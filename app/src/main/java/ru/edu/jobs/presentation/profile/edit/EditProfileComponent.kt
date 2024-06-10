package ru.edu.jobs.presentation.profile.edit

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Role

interface EditProfileComponent {

    val model: StateFlow<EditProfileStore.State>

    fun onBackClick()
    fun onSaveClick()

    fun onChangeLastName(lastName: String)
    fun onChangeFirstName(firstName: String)
    fun onChangePhone(phone: String)
    fun onChangeType(type: Role)
    fun onChangeEmail(email: String)
    fun onChangeUsername(username: String)
    fun onChangeAvatar(avatar: String)

}