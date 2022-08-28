package com.sm.batterymanager.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sm.batterymanager.R
import com.sm.batterymanager.databinding.ActivitySplashBinding
import java.util.*
import kotlin.concurrent.timerTask

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textArray = arrayOf(
            "Manage your phone battery",
            "Make your battery powerful",
            "Make your battery safe"
        )

        for (index in 0..2) {
            helpTextGenerator((((index*2)+1) * 1000).toLong(), textArray[index])
        }

        Timer().schedule(timerTask {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 7000)
    }

    private fun helpTextGenerator(delayTime: Long, text: String) {
        Timer().schedule(timerTask {
            runOnUiThread(timerTask {
                binding.helpTextView.text = text
            })
        }, delayTime)
    }

    override fun onStop() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        super.onStop()
    }
}