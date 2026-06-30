package com.timecalendar.app.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import java.util.TimeZone

data class SystemCalendarEvent(
    val id: Long,
    val title: String,
    val startMillis: Long,
    val endMillis: Long,
    val allDay: Boolean,
    val calendarName: String,
    val color: Int
)

data class SystemCalendar(
    val id: Long,
    val name: String,
    val displayName: String,
    val accountName: String,
    val color: Int
)

object SystemCalendarHelper {

    fun getCalendars(context: Context): List<SystemCalendar> {
        val calendars = mutableListOf<SystemCalendar>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR
        )

        try {
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                "${CalendarContract.Calendars.VISIBLE} = 1",
                null,
                null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    calendars.add(
                        SystemCalendar(
                            id = cursor.getLong(0),
                            name = cursor.getString(2) ?: "",
                            displayName = cursor.getString(1) ?: "",
                            accountName = cursor.getString(3) ?: "",
                            color = cursor.getInt(4)
                        )
                    )
                }
            }
        } catch (_: SecurityException) { }

        return calendars
    }

    fun getUpcomingEvents(context: Context, daysAhead: Int = 30): List<SystemCalendarEvent> {
        val events = mutableListOf<SystemCalendarEvent>()
        val now = System.currentTimeMillis()
        val end = now + daysAhead.toLong() * 24 * 60 * 60 * 1000

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
            CalendarContract.Instances.EVENT_COLOR
        )

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, now)
        ContentUris.appendId(builder, end)

        try {
            context.contentResolver.query(
                builder.build(),
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    events.add(
                        SystemCalendarEvent(
                            id = cursor.getLong(0),
                            title = cursor.getString(1) ?: "",
                            startMillis = cursor.getLong(2),
                            endMillis = cursor.getLong(3),
                            allDay = cursor.getInt(4) == 1,
                            calendarName = cursor.getString(5) ?: "",
                            color = cursor.getInt(6)
                        )
                    )
                }
            }
        } catch (_: SecurityException) { }

        return events
    }

    fun getEventsForDate(context: Context, dateMillis: Long): List<SystemCalendarEvent> {
        val dayStart = DateUtils.getDayStart(dateMillis)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return getUpcomingEvents(context, 60).filter {
            it.startMillis in dayStart..dayEnd || (it.allDay && it.startMillis <= dayEnd && it.endMillis >= dayStart)
        }
    }

    fun addEventToSystemCalendar(
        context: Context,
        title: String,
        startMillis: Long,
        allDay: Boolean = true
    ): Long? {
        val calIds = getCalendars(context)
        val calId = calIds.firstOrNull()?.id ?: return null

        val values = android.content.ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.ALL_DAY, if (allDay) 1 else 0)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            if (allDay) {
                put(CalendarContract.Events.DTEND, startMillis + 24 * 60 * 60 * 1000)
            }
        }

        return try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri?.lastPathSegment?.toLongOrNull()
        } catch (_: Exception) { null }
    }
}
