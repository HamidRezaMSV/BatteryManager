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

class BatteryAlarmService : Service() {

    companion object {
        const val CHANNEL_ID = "BatteryManagerChannel"
        const val CHANNEL_NAME = "BatteryManagerService"
        const val NOTIFICATION_ID = 1
    }

    private var manager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startNotification()

        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return START_STICKY // service become active in every situation.
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
            manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun startNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Loading ...")
            .setContentText("Wait for battery data")
            .setSmallIcon(R.drawable.battery_logo)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(batteryLevel: Int, plugState: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(plugState)
            .setContentText("Battery charge : $batteryLevel%")
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
            var plugState =
                if (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 5) == 0) {
                    getString(R.string.battery_state_plug_out)
                } else {
                    getString(R.string.battery_state_plug_in)
                }

            if (batteryLevel != null && batteryLevel > 99) {
                startAlarm()
                plugState = getString(R.string.battery_state_full)
            }

            updateNotification(batteryLevel!!, plugState)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}