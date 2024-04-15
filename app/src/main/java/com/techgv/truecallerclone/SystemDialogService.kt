package com.techgv.truecallerclone

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat


class SystemDialogService : Service(), View.OnTouchListener {
    private var windowManager: WindowManager? = null

    private var floatyView: View? = null

    private var binder: IBinder? = null

    override fun onBind(intent: Intent): IBinder? {
        stopForeground(STOP_FOREGROUND_REMOVE)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MainService", "OnStartCommand")

        intent?.let {
            val number = it.getStringExtra("number")
            floatyView?.let{ view->
                view.findViewById<TextView>(R.id.name).text = if(number != "-1") number else " "
            }
        }
        return if (intent == null) {
            START_STICKY_COMPATIBILITY
        } else {
            START_STICKY
        }
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        prepareAndStartForeground(this)
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MainService", "OnCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this)
        }
        prepareAndStartForeground(this)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        addOverlayView()
    }

    private fun addOverlayView() {
        val params: WindowManager.LayoutParams

        val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            0,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        val interceptorLayout: FrameLayout = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)

                if (event.action == KeyEvent.ACTION_DOWN) {
                    // Check if the HOME button is pressed

                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.v(TAG, "BACK Button Pressed")

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                        return true
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event)
            }
        }

        val inflater = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)

        floatyView = inflater.inflate(R.layout.floating_view, interceptorLayout)
        floatyView?.let {
            it.findViewById<ImageView>(R.id.cancel).setOnClickListener {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            it.findViewById<Button>(R.id.view_profile).setOnClickListener {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
        }

        floatyView!!.setOnTouchListener(this)
        windowManager!!.addView(floatyView, params)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (floatyView != null) {
            windowManager!!.removeView(floatyView)

            floatyView = null
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        view.performClick()

        Log.v(TAG, "onTouch...")

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        // Kill service
        onDestroy()

        return true
    }

    companion object {
        private val TAG: String = SystemDialogService::class.java.simpleName
    }

    private fun prepareAndStartForeground(context: Context) {
        try {
            val intent = Intent(context, SystemDialogService::class.java)
            ContextCompat.startForegroundService(context, intent)

            val notification = buildNotification(context)
            ServiceCompat.startForeground(
                this,
                11,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } catch (e: Exception) {
            Log.e("CallReceiver", "startForegroundNotification: " + e.message)
        }
    }

    private fun buildNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, "FileUploadWorkerChannel")
            .setContentTitle("Something")
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "FileUploadWorkerChannel",
            "Default",
            NotificationManager.IMPORTANCE_LOW
        )

        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }


}