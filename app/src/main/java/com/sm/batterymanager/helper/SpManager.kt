package com.sm.batterymanager.helper

import android.content.Context
import android.content.SharedPreferences

class SpManager {

    companion object{

        private var sharedPreferences : SharedPreferences? = null
        private var editor : SharedPreferences.Editor? = null
        private const val spb = "SHARED_PREFERENCE_BOOLEAN"
        private const val isServiceOn = "isServiceOn"
        private const val isLowBatteryAlarmOn = "isLowBatteryAlarmOn"
        private const val lowBatteryAlarmPercent = "lowBatteryAlarmPercent"

        fun setServiceState(context: Context , isOn:Boolean?){
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
            editor?.putBoolean(isServiceOn , isOn!!) // if not null do my work
            editor?.apply()
        }

        fun isServiceOn(context: Context) : Boolean? {
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            return sharedPreferences?.getBoolean(isServiceOn , false)
        }

        fun setLowBatteryAlarmState(context: Context , isOn : Boolean?){
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
            editor?.putBoolean(isLowBatteryAlarmOn , isOn!!)
            editor?.apply()
        }

        fun isLowBatteryAlarmOn(context: Context) : Boolean? {
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            return sharedPreferences?.getBoolean(isLowBatteryAlarmOn , false)
        }

        fun setLowBatteryAlarmPercent(context: Context , percent : Int?){
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
            editor?.putInt(lowBatteryAlarmPercent , percent!!)
            editor?.apply()
        }

        fun getLowBatteryAlarmPercent(context: Context) : Int? {
            sharedPreferences = context.getSharedPreferences(spb , Context.MODE_PRIVATE)
            return sharedPreferences?.getInt(lowBatteryAlarmPercent , 0)
        }

    }
}