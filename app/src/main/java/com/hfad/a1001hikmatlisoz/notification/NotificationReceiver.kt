package com.hfad.a1001hikmatlisoz.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfad.a1001hikmatlisoz.NotificationService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = NotificationService(context)
        service.showNotification()
    }
}