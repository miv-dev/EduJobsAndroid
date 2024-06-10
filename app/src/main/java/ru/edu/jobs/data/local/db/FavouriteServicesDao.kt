package ru.edu.jobs.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.data.local.model.ServiceDBModel

@Dao
interface FavouriteServicesDao {

    @Query("SELECT * FROM fav_services")
    fun getFavouriteServices(): Flow<List<ServiceDBModel>>

    @Query("SELECT EXISTS (SELECT * FROM fav_services WHERE id=:serviceId LIMIT 1)")
    fun observeIsFavourite(serviceId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourite(serviceDBModel: ServiceDBModel)

    @Query("DELETE FROM fav_services WHERE id=:serviceId")
    suspend fun removeFromFavourite(serviceId: Int)

    @Query("DELETE FROM fav_services")
    suspend fun removeAll()

}