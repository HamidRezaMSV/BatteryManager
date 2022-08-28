package com.sm.batterymanager.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.sm.batterymanager.R
import com.sm.batterymanager.helper.SpManager

class LowBatteryService : Service() {

    companion object{
        const val CHANNEL_ID = "LowBatteryChannel"
        const val CHANNEL_NAME = "LowBatteryService"
        const val NOTIFICATION_ID = 2
    }

    private var manager : NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startNotification()

        registerReceiver(batteryInfoReceiver , IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel =
                NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_MIN)
            manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun startNotification() {
        val targetLevel = SpManager.getLowBatteryAlarmPercent(applicationContext)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Low Battery Alarm Activated")
            .setContentText("Alarm plays on battery level ${targetLevel}%")
            .setSmallIcon(R.drawable.battery_logo)
            .build()

        startForeground(NOTIFICATION_ID , notification)
    }

    private fun updateNotification(){
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Warning")
            .setContentText("Low Battery,Please charge your phone")
            .setSmallIcon(R.drawable.battery_logo)
            .build()

        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun startAlarm() {
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ring = RingtoneManager.getRingtone(applicationContext, alarm)
        ring.play()

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(1500)
        }
    }

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryLevel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)

            val targetLevel = SpManager.getLowBatteryAlarmPercent(applicationContext)

            if (batteryLevel != null && batteryLevel == targetLevel) {
                updateNotification()
                startAlarm()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}