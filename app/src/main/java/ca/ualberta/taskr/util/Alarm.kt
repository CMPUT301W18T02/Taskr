package ca.ualberta.taskr.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import android.widget.Toast
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback


/**
 *  3/26/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 *  https://stackoverflow.com/questions/15572530/android-alarm-start-on-boot
 */

/**
 * Alarm class. Used to handle notifications and updating data when the application is asleep
 * and not active
 */
class Alarm : BroadcastReceiver() {

    /**
     * When an alarm is recieved
     * @param context the [Context]
     * @param intent the delivered [Intent]
     */
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")
        wl.acquire(1000)
        CachingRetrofit(context).getTasks(object : Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {}
        }).execute()
        Log.i("ALARM", "updated tasks")
        wl.release()
    }

    /**
     * Set the alarm
     * @param context the [Context] of the alarm
     */
    fun setAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, Alarm::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        Log.i("ALARM", "ALARM Manager started")
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (1000 * 60).toLong(), pi) // Millisec * Second
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, Alarm::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}