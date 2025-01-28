package com.example.testschedule.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.testschedule.alarm_manager.DefaultAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = ScheduleWidget()
}