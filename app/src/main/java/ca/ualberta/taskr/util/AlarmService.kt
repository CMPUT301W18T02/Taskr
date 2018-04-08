package ca.ualberta.taskr.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *  3/26/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * AlarmService class. This class acts like a background service for recieving alarms
 */
class AlarmService : BroadcastReceiver() {
    private val alarm = Alarm()
    override fun onReceive(context: Context, intent: Intent) {
        alarm.setAlarm(context)
    }
}