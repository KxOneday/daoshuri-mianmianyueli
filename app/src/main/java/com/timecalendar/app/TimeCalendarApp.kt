package com.timecalendar.app

import android.app.Application
import com.timecalendar.app.data.local.AppDatabase
import com.timecalendar.app.reminder.ReminderManager

class TimeCalendarApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ReminderManager.createNotificationChannel(this)
    }

    companion object {
        lateinit var instance: TimeCalendarApp
            private set
    }
}
