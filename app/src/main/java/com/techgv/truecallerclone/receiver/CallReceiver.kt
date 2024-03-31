package com.techgv.truecallerclone.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat.startForegroundService
import com.techgv.truecallerclone.SystemDialogService


class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val number = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER, "-1")
        Log.d("CallReceiver", number.toString())

        val service = Intent(context, SystemDialogService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(service)
        } else {
            context?.startService(service)
        }

    }

}
