package com.sm.batterymanager.activity

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.sm.batterymanager.R
import com.sm.batterymanager.adapter.BatteryUsageAdapter
import com.sm.batterymanager.databinding.ActivityUsageBatteryBinding
import com.sm.batterymanager.model.BatteryModel
import com.sm.batterymanager.utils.BatteryUsage

class UsageBatteryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUsageBatteryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageBatteryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val batteryUsage = BatteryUsage(this)
        val batteryPercentArray : MutableList<BatteryModel> = ArrayList()

        for (item in batteryUsage.getUsageStateList()){

            if (item.totalTimeInForeground > 0){
                val obj = BatteryModel()
                obj.packageName = item.packageName
                obj.percentUsage =
                    (item.totalTimeInForeground.toFloat() / batteryUsage.getTotalTime().toFloat() * 100).toInt()
                batteryPercentArray += obj
            }
        }

        val adapter = BatteryUsageAdapter(this ,batteryPercentArray , batteryUsage.getTotalTime())

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        binding.imgBackToMain.setOnClickListener { finish() }
    }

    override fun onStop() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        super.onStop()
    }
}