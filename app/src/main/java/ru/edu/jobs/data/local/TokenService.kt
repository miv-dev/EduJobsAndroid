package ru.edu.jobs.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class TokenService @Inject constructor(
    private val sp: SharedPreferences
) {



    fun loadAccessToken() = sp.getString(ACCESS_TOKEN_KEY, "")
    fun loadRefreshToken() = sp.getString(REFRESH_TOKEN_KEY, "")





    fun setAccessToken(access: String?) {

        with(sp.edit()) {
            putString(ACCESS_TOKEN_KEY, access)
            commit()
        }
    }

    fun setRefreshToken(refresh: String?) {

        with(sp.edit()) {
            putString(REFRESH_TOKEN_KEY, refresh)
            commit()
        }
    }

    companion object {
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val ACCESS_TOKEN_KEY = "access_token"
    }
}