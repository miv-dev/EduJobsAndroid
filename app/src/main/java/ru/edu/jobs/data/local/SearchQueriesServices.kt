package ru.edu.jobs.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class SearchQueriesServices @Inject constructor(
    private val sp: SharedPreferences
) {
    fun loadSearchQueries(): List<String> {
        val queries = sp.getString(SEARCH_QUERIES_KEY, "")

        return queries?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    fun setSearchQueries(queries: List<String>) {
        with(sp.edit()) {
            putString(SEARCH_QUERIES_KEY, queries.joinToString(","))
            commit()
        }
    }

    companion object {
        const val SEARCH_QUERIES_KEY = "search_queries"
    }
}