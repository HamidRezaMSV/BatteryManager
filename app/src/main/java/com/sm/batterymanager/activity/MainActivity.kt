package com.sm.batterymanager.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.sm.batterymanager.R
import com.sm.batterymanager.databinding.ActivityMainBinding
import com.sm.batterymanager.helper.SpManager
import com.sm.batterymanager.service.BatteryAlarmService
import com.sm.batterymanager.service.LowBatteryService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDrawer()
        serviceConfig()
        lowBatteryAlarmConfig()

        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun initDrawer(){
        binding.imgMenu.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.END)
        }

        binding.incDrawer.txtAppBatteryUsage.setOnClickListener {
            startActivity(Intent(this, UsageBatteryActivity::class.java))
            binding.drawer.closeDrawer(GravityCompat.END)
        }
    }

    private fun serviceConfig(){
        if(SpManager.isServiceOn(this) == true){
            binding.incDrawer.txtService.text = getString(R.string.service_is_on)
            binding.incDrawer.serviceSwitch.isChecked = true
        }else{
            binding.incDrawer.txtService.text = getString(R.string.service_is_off)
            binding.incDrawer.serviceSwitch.isChecked = false
        }

        binding.incDrawer.serviceSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            SpManager.setServiceState(this, isChecked)
            if (isChecked){
                startFullyChargeService()
                binding.incDrawer.txtService.text = getString(R.string.service_is_on)
                Toast.makeText(this, getString(R.string.service_is_turn_on), Toast.LENGTH_SHORT).show()
            }else{
                stopFullyChargeService()
                binding.incDrawer.txtService.text = getString(R.string.service_is_off)
                Toast.makeText(this, getString(R.string.service_is_turn_off), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lowBatteryAlarmConfig(){
        if (SpManager.isLowBatteryAlarmOn(this) == true){
            binding.incDrawer.txtLowBatteryState.text = getString(R.string.low_battery_alarm_is_on)
            binding.incDrawer.lowBatterySwitch.isChecked = true
            binding.incDrawer.lowBatteryLayout.visibility = View.VISIBLE
            binding.incDrawer.lowBatterySeekBar.progress = SpManager.getLowBatteryAlarmPercent(this)!!
            binding.incDrawer.lowBatteryNumTxt.text = "${SpManager.getLowBatteryAlarmPercent(this)!!} %"
        }else{
            binding.incDrawer.txtLowBatteryState.text = getString(R.string.low_battery_alarm_is_off)
            binding.incDrawer.lowBatterySwitch.isChecked = false
            binding.incDrawer.lowBatteryLayout.visibility = View.INVISIBLE
        }

        binding.incDrawer.lowBatterySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            SpManager.setLowBatteryAlarmState(this , isChecked)

            if (isChecked){
                startLowBatteryService()
                binding.incDrawer.txtLowBatteryState.text = getString(R.string.low_battery_alarm_is_on)
                binding.incDrawer.lowBatteryLayout.visibility = View.VISIBLE
                binding.incDrawer.lowBatterySeekBar.progress = SpManager.getLowBatteryAlarmPercent(this)!!
                binding.incDrawer.lowBatteryNumTxt.text = "${SpManager.getLowBatteryAlarmPercent(this)!!} %"
            }else{
                stopLowBatteryService()
                binding.incDrawer.txtLowBatteryState.text = getString(R.string.low_battery_alarm_is_off)
                binding.incDrawer.lowBatteryLayout.visibility = View.INVISIBLE
            }
        }

        binding.incDrawer.lowBatterySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.incDrawer.lowBatteryNumTxt.text = "$progress %"
                SpManager.setLowBatteryAlarmPercent(this@MainActivity , progress )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startFullyChargeService(){
        val serviceIntent = Intent(this , BatteryAlarmService::class.java)
        ContextCompat.startForegroundService(this , serviceIntent)
    }

    private fun stopFullyChargeService(){
        val serviceIntent = Intent(this , BatteryAlarmService::class.java)
        stopService(serviceIntent)
    }

    private fun startLowBatteryService(){
        val intent = Intent(this , LowBatteryService::class.java)
        ContextCompat.startForegroundService(this,intent)
    }

    private fun stopLowBatteryService(){
        val intent = Intent(this , LowBatteryService::class.java)
        stopService(intent)
    }

    private val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryLevel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            binding.apply {
                txtCharge.text = "$batteryLevel %"
                txtTemp.text = (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    ?.div(10)).toString() + " °C"
                txtVoltage.text = (intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    ?.div(1000)).toString() + " volt"
                txtTechnology.text = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                if (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 5) == 0) {
                    txtPlug.text = getString(R.string.plug_out)
                } else {
                    txtPlug.text = getString(R.string.plug_in)
                }
            }
            binding.circularProgressBar.apply {
                progressMax = 100f
                setProgressWithAnimation(batteryLevel!!.toFloat(), 2000)
            }
            when (intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
                BatteryManager.BATTERY_HEALTH_DEAD -> {
                    binding.txtHealth.text = getString(R.string.battery_dead)
                    binding.txtHealth.setTextColor(Color.parseColor("#FFF44336"))
                    binding.imgHealth.setImageResource(R.drawable.dead)
                }
                BatteryManager.BATTERY_HEALTH_COLD -> {
                    binding.txtHealth.text = getString(R.string.battery_cold)
                    binding.txtHealth.setTextColor(Color.parseColor("#FF02E2FF"))
                    binding.imgHealth.setImageResource(R.drawable.cold)
                }
                BatteryManager.BATTERY_HEALTH_GOOD -> {
                    binding.txtHealth.text = getString(R.string.battery_good)
                    binding.txtHealth.setTextColor(Color.parseColor("#FF06F10F"))
                    binding.imgHealth.setImageResource(R.drawable.good)
                }
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                    binding.txtHealth.text = getString(R.string.battery_overheat)
                    binding.txtHealth.setTextColor(Color.parseColor("#FFFF1100"))
                    binding.imgHealth.setImageResource(R.drawable.overheating)
                }
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                    binding.txtHealth.text = getString(R.string.battery_over_voltage)
                    binding.txtHealth.setTextColor(Color.parseColor("#FFFF9800"))
                    binding.imgHealth.setImageResource(R.drawable.voltage)
                }
                else -> {
                    binding.txtHealth.text = getString(R.string.battery_else)
                    binding.txtHealth.setTextColor(Color.parseColor("#FFFFE501"))
                    binding.imgHealth.setImageResource(R.drawable.unknown)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.END)){
            binding.drawer.closeDrawer(GravityCompat.END)
            return
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Do you want to close application ?")
            .setCancelable(true) // touch other place -> close dialog
            .setPositiveButton("Yes") { dialog, id -> finish() }
            .setNegativeButton("No") { dialog , id -> dialog.cancel() }

        val alert = dialogBuilder.create()
        alert.setTitle("Exit")
        alert.show()
    }

    override fun onStart() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        super.onStart()
    }
}