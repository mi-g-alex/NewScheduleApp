package com.example.testschedule.alarm_manager

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import java.util.Calendar

class DefaultAlarmScheduler(private val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE)
            as AlarmManager


    fun schedule(): Boolean {
        if(Build.VERSION.SDK_INT < 23) return false
        Log.d("AlarmManager","Schedule DefaultAlarmScheduler")
        val pendingIntent = SchedulerBroadcastReceiver.createPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        val t = Calendar.getInstance().timeInMillis + 60 * 60 * 1000L
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            t,
            pendingIntent
        )
        Log.d("AlarmManager","Schedule DefaultAlarmScheduler started")
        return true
    }
}