package com.muei.apm.fasterwho

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat

class ForegroundLocationService(/*context: Context, pendingIntent: PendingIntent*/): Service() {
    private val CHANNEL_ID = "ForegroundLocationService FasterWho"
    var pendingIntent : PendingIntent? = null
    var context : Context? = null
    private val binder : IBinder = BinderLocationService()

    inner class BinderLocationService: Binder(){
        fun getService(): ForegroundLocationService = this@ForegroundLocationService
    }
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    /*override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("FasterWho")
                .setContentText("Se está usando la ubicación")
                .setContentIntent(pendingIntent)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        startForeground(1, notification)
    }*/
    fun startService(context: Context,intent:PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
            val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("FasterWho")
                    .setContentText("Se está usando la ubicación")
                    .setContentIntent(intent)
                    .build()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
                startForeground(1, notification)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundLocationService::class.java)
            context.stopService(stopIntent)
        }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context?.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("FasterWho")
                .setContentText("Se está usando la ubicación")
                .setContentIntent(pendingIntent)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        startForeground(2, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"Se para el servicio",Toast.LENGTH_LONG).show()
    }
}