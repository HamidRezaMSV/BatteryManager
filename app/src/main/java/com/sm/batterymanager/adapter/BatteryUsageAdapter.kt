package com.sm.batterymanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sm.batterymanager.R
import com.sm.batterymanager.model.BatteryModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class BatteryUsageAdapter(
    private val context: Context,
    private val primaryList: MutableList<BatteryModel>,
    private val totalTime: Long
) : RecyclerView.Adapter<BatteryUsageAdapter.ViewHolder>() {

    private var finalData: MutableList<BatteryModel> = ArrayList()

    init {
        finalData = getSortedList(primaryList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_battery_usage, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedItem = finalData[position]
        holder.txtPercent.text = "${selectedItem.percentUsage}%"
        holder.txtTime.text = selectedItem.timeUsage.toString()
        holder.txtAppName.text = getAppName(selectedItem.packageName.toString())
        holder.progressBar.progress = selectedItem.percentUsage
        holder.imageView.setImageDrawable(getAppIcon(selectedItem.packageName.toString()))
    }

    override fun getItemCount(): Int {
        return finalData.size
    }

    private fun getSortedList(batteryPercentArray: MutableList<BatteryModel>): MutableList<BatteryModel> {

        val finalList: MutableList<BatteryModel> = ArrayList()

        val sortedList = batteryPercentArray
            .groupBy { it.packageName }
            .mapValues { entry -> entry.value.sumBy { it.percentUsage } }.toList()
            .sortedWith(compareBy { it.second }).reversed()

        for (item in sortedList) {
            val obj = BatteryModel()

            val timePerApp = ( item.second.toFloat() / 100 ) * totalTime.toFloat() / 1000 / 60
            val hour = timePerApp / 60
            val min = timePerApp % 60

            obj.packageName = item.first
            obj.percentUsage = item.second
            obj.timeUsage = "${hour.roundToInt()}h : ${min.roundToInt()}m"
            finalList += obj
        }

        return finalList
    }

    private fun getAppName(packageName: String): String {
        val pm = context.applicationContext.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }

        return (if (ai != null) pm.getApplicationLabel(ai) else "Unknown") as String
    }

    private fun getAppIcon(packageName: String) : Drawable? {
        var icon : Drawable? = null
        try {
           icon = context.packageManager.getApplicationIcon(packageName)
        }catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
        }
        return icon
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPercent: TextView = itemView.findViewById(R.id.txt_percent)
        val txtTime: TextView = itemView.findViewById(R.id.txt_time)
        val txtAppName: TextView = itemView.findViewById(R.id.txt_app_name)
        val progressBar : ProgressBar = itemView.findViewById(R.id.progress_bar)
        val imageView : CircleImageView = itemView.findViewById(R.id.imageView)
    }
}