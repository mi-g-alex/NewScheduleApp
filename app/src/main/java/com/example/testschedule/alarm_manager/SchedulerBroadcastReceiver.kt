package com.example.testschedule.alarm_manager

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.testschedule.widget.ScheduleWidget
import com.example.testschedule.widget.WidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchedulerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmManager", "onReceive")
        CoroutineScope(Dispatchers.IO).launch {
            ScheduleWidget().updateAll(context)
        }
    }

    companion object {

        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SchedulerBroadcastReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                200,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}