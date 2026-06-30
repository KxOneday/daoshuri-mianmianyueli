package com.timecalendar.app

import android.app.Application
import com.timecalendar.app.data.local.AppDatabase

class TimeCalendarApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TimeCalendarApp
            private set
    }
}
