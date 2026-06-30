package com.timecalendar.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.timecalendar.app.R
import com.timecalendar.app.data.local.AppDatabase
import com.timecalendar.app.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TimeCalendarWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val eventList = db.countdownEventDao().getAllVisible().firstOrNull()

                    if (eventList != null && eventList.isNotEmpty()) {
                        val today = DateUtils.getTodayStart()
                        val nearest = eventList
                            .map { event ->
                                val effectiveDate = DateUtils.getEffectiveTargetDate(event.targetDate, event.isRepeatYearly)
                                event to effectiveDate
                            }
                            .filter { (_, effectiveDate) -> effectiveDate >= today }
                            .minByOrNull { (_, effectiveDate) -> effectiveDate }
                            ?.first ?: eventList.first()

                        val effectiveDate = DateUtils.getEffectiveTargetDate(nearest.targetDate, nearest.isRepeatYearly)
                        val days = DateUtils.getDaysFromNow(effectiveDate)
                        views.setTextViewText(R.id.widget_event_name, nearest.title)
                        views.setTextViewText(R.id.widget_days, "还有 $days 天")
                    } else {
                        views.setTextViewText(R.id.widget_event_name, "暂无倒数事件")
                        views.setTextViewText(R.id.widget_days, "--")
                    }
                } catch (e: Exception) {
                    views.setTextViewText(R.id.widget_event_name, "时光历")
                    views.setTextViewText(R.id.widget_days, "--")
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
