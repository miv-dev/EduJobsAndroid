package ru.edu.jobs

import android.app.Application
import ru.edu.jobs.di.ApplicationComponent
import ru.edu.jobs.di.DaggerApplicationComponent

class EduJobsApp : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
            .factory().create(this)
    }

}