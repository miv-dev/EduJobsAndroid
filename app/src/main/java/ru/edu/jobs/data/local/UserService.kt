package ru.edu.jobs.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class UserService @Inject constructor(
    private val sp: SharedPreferences
) {
    fun loadLastLoggedUserId(): Int {
        return  sp.getInt(LAST_LOGGED_USER_KEY, -1)
    }

    fun setLastLoggedUserId(id: Int) {
        with(sp.edit()) {
            putInt(LAST_LOGGED_USER_KEY, id)
            commit()
        }
    }

    companion object {
        const val LAST_LOGGED_USER_KEY = "last_logged_user_id"
    }
}