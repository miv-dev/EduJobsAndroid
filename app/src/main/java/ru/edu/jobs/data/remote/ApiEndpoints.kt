package ru.edu.jobs.data.remote

abstract class ApiEndpoints {


    companion object {
        fun getServiceUrl(id: Int): String = "/api/services/$id/"
        fun getSearchUrl(query: String): String = "/api/services/search/?query=$query"
        fun getUpdateUserUrl(id: Int): String = "/api/users/$id/"
        const val PARSED_SERVICES = "api/parsed/services/"
        const val WELCOME = "/api/welcome/"
        const val LOGIN = "/auth/login/"
        const val REGISTER = "/auth/register/"
        const val CURRENT_USER = "/api/users/current/"
        const val SERVICES = "/api/services/"
        const val VIEWED_SERVICES = "/api/services/viewed/"
        const val USER_SERVICES = "/api/services/own/"
        const val ADD_VIEWED_SERVICES = "/api/services/viewed/add/"
        const val DEPARTMENTS = "/api/department/"
    }
}