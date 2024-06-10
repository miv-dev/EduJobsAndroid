package ru.edu.jobs.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.edu.jobs.R
import ru.edu.jobs.data.local.db.FavouriteDatabase
import ru.edu.jobs.data.local.db.FavouriteServicesDao
import ru.edu.jobs.data.repository.AuthRepositoryImpl
import ru.edu.jobs.data.repository.FavouriteServicesRepositoryImpl
import ru.edu.jobs.data.repository.MyServicesRepositoryImpl
import ru.edu.jobs.data.repository.ParsedServicesRepositoryImpl
import ru.edu.jobs.data.repository.ServiceRepositoryImpl
import ru.edu.jobs.data.repository.UniversityRepositoryImpl
import ru.edu.jobs.data.repository.UserRepositoryImpl
import ru.edu.jobs.data.repository.ViewedServicesRepositoryImpl
import ru.edu.jobs.data.repository.WelcomeRepositoryImpl
import ru.edu.jobs.domain.repository.AuthRepository
import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import ru.edu.jobs.domain.repository.MyServicesRepository
import ru.edu.jobs.domain.repository.ParsedServicesRepository
import ru.edu.jobs.domain.repository.ServiceRepository
import ru.edu.jobs.domain.repository.UniversityRepository
import ru.edu.jobs.domain.repository.UserRepository
import ru.edu.jobs.domain.repository.ViewedServicesRepository
import ru.edu.jobs.domain.repository.WelcomeRepository

@Module
interface DataModule {
    @[ApplicationScope Binds]
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @[ApplicationScope Binds]
    fun bindViewedServicesRepository(impl: ViewedServicesRepositoryImpl): ViewedServicesRepository

    @[ApplicationScope Binds]
    fun bindParsedServicesRepository(impl: ParsedServicesRepositoryImpl): ParsedServicesRepository

    @[ApplicationScope Binds]
    fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository

    @[ApplicationScope Binds]
    fun bindMyServicesRepository(impl: MyServicesRepositoryImpl): MyServicesRepository

    @[ApplicationScope Binds]
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @[ApplicationScope Binds]
    fun bindFavouriteRepository(impl: FavouriteServicesRepositoryImpl): FavouriteServicesRepository

    @[ApplicationScope Binds]
    fun bindUniversityRepository(impl: UniversityRepositoryImpl): UniversityRepository

    @[ApplicationScope Binds]
    fun bindWelcomeRepository(impl: WelcomeRepositoryImpl): WelcomeRepository

    companion object {

        @[ApplicationScope Provides]
        fun provideSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )


        @[ApplicationScope Provides]
        fun provideFavoriteDatabase(context: Context): FavouriteDatabase {
            return FavouriteDatabase.getInstance(context)
        }

        @[ApplicationScope Provides]
        fun provideFavouriteCitiesDao(database: FavouriteDatabase): FavouriteServicesDao =
            database.favouriteServicesDao()
    }


}