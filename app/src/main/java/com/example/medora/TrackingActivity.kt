package com.example.medora

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.network.ApiService
import com.example.medora.network.HealthAnalytics
import com.example.medora.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrackingActivity : AppCompatActivity() {

    private lateinit var pieHeart: PieChart
    private lateinit var barBP: BarChart
    private lateinit var barSteps: BarChart
    private lateinit var barCalories: BarChart
    private lateinit var barSleep: BarChart
    private lateinit var pieOxygen: PieChart

    private lateinit var tvHeartValue: TextView
    private lateinit var tvHeartStatus: TextView
    private lateinit var tvStepsStatus: TextView
    private lateinit var tvCaloriesStatus: TextView
    private lateinit var tvSleepStatus: TextView
    private lateinit var tvOxygenStatus: TextView
    private lateinit var tvGreeting: TextView

    private lateinit var btnDaily: Button
    private lateinit var btnWeekly: Button
    private lateinit var btnMonthly: Button
    private lateinit var btnYearly: Button

    private lateinit var tabTrends: TextView
    private lateinit var tabStats: TextView
    private lateinit var tabInsights: TextView

    private lateinit var cardHeart: CardView
    private lateinit var cardBP: CardView
    private lateinit var cardSteps: CardView
    private lateinit var cardCalories: CardView
    private lateinit var cardSleep: CardView
    private lateinit var cardOxygen: CardView

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    
    private var currentTimeFilter = "week"
    private var currentTab = "Trends"
    private var analyticsCache: HealthAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        apiService = RetrofitClient.getApiService()
        sessionManager = SessionManager
        
        bindViews()
        setupGreeting()
        setupButtons()
        setupTabs()
        setupCharts()
        setupCardClicks()
        setupBottomNavigation()
        
        // Load data from backend
        loadDataForPeriod(currentTimeFilter)
    }
    
    private fun setupGreeting() {
        val username = sessionManager.getUserName(this) ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        tvGreeting.text = "$greeting, $username!"
    }

    private fun bindViews() {
        pieHeart = findViewById(R.id.pieHeart)
        barBP = findViewById(R.id.barBP)
        barSteps = findViewById(R.id.barSteps)
        barCalories = findViewById(R.id.barCalories)
        barSleep = findViewById(R.id.barSleep)
        pieOxygen = findViewById(R.id.pieOxygen)

        tvHeartValue = findViewById(R.id.tvHeartValue)
        tvHeartStatus = findViewById(R.id.tvHeartStatus)
        tvStepsStatus = findViewById(R.id.tvStepsStatus)
        tvCaloriesStatus = findViewById(R.id.tvCaloriesStatus)
        tvSleepStatus = findViewById(R.id.tvSleepStatus)
        tvOxygenStatus = findViewById(R.id.tvOxygenStatus)
        tvGreeting = findViewById(R.id.tvGreeting)

        btnDaily = findViewById(R.id.btnDaily)
        btnWeekly = findViewById(R.id.btnWeekly)
        btnMonthly = findViewById(R.id.btnMonthly)
        btnYearly = findViewById(R.id.btnYearly)

        tabTrends = findViewById(R.id.tabTrends)
        tabStats = findViewById(R.id.tabStats)
        tabInsights = findViewById(R.id.tabInsights)

        cardHeart = findViewById(R.id.cardHeart)
        cardBP = findViewById(R.id.cardBP)
        cardSteps = findViewById(R.id.cardSteps)
        cardCalories = findViewById(R.id.cardCalories)
        cardSleep = findViewById(R.id.cardSleep)
        cardOxygen = findViewById(R.id.cardOxygen)
    }

    private fun setupButtons() {
        btnDaily.setOnClickListener {
            setActiveFilter(btnDaily)
            currentTimeFilter = "day"
            loadDataForPeriod(currentTimeFilter)
        }
        btnWeekly.setOnClickListener {
            setActiveFilter(btnWeekly)
            currentTimeFilter = "week"
            loadDataForPeriod(currentTimeFilter)
        }
        btnMonthly.setOnClickListener {
            setActiveFilter(btnMonthly)
            currentTimeFilter = "month"
            loadDataForPeriod(currentTimeFilter)
        }
        btnYearly.setOnClickListener {
            setActiveFilter(btnYearly)
            currentTimeFilter = "year"
            loadDataForPeriod(currentTimeFilter)
        }
    }

    private fun setupTabs() {
        tabTrends.setOnClickListener {
            setActiveTab(tabTrends)
            currentTab = "Trends"
            refreshDataForTab()
        }
        tabStats.setOnClickListener {
            setActiveTab(tabStats)
            currentTab = "Statistics"
            refreshDataForTab()
        }
        tabInsights.setOnClickListener {
            setActiveTab(tabInsights)
            currentTab = "Insights"
            showInsightsView()
        }
    }

    private fun setActiveTab(activeTab: TextView) {
        val all = listOf(tabTrends, tabStats, tabInsights)
        all.forEach { tab ->
            if (tab == activeTab) {
                tab.setBackgroundResource(R.drawable.tab_active_left)
                tab.setTextColor(Color.WHITE)
            } else {
                tab.setBackgroundResource(R.drawable.tab_middle)
                tab.setTextColor(Color.parseColor("#527A89"))
            }
        }
    }

    private fun refreshDataForTab() {
        loadDataForPeriod(currentTimeFilter)
    }

    private fun showInsightsView() {
        Toast.makeText(this, "AI Insights - Analyzing your health data...", Toast.LENGTH_SHORT).show()
        // In production, this would show AI-generated insights
    }
    
    // Backend Integration Functions
    private fun loadDataForPeriod(period: String) {
        lifecycleScope.launch {
            try {
                Log.d("Tracking", "Fetching analytics for period: $period")
                val response = apiService.getHealthAnalytics(period)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    analyticsCache = response.body()?.data
                    analyticsCache?.let { displayAnalytics(it) }
                } else {
                    Log.e("Tracking", "API error: ${response.message()}")
                    Toast.makeText(this@TrackingActivity, "Failed to load health data", Toast.LENGTH_SHORT).show()
                    // Load sample data as fallback
                    loadSampleData()
                }
            } catch (e: Exception) {
                Log.e("Tracking", "Network error: ${e.message}", e)
                Toast.makeText(this@TrackingActivity, "Network error. Showing sample data.", Toast.LENGTH_SHORT).show()
                // Load sample data as fallback
                loadSampleData()
            }
        }
    }
    
    private fun displayAnalytics(analytics: HealthAnalytics) {
        // Heart Rate
        val avgHeart = analytics.averages.heartRate
        tvHeartValue.text = "$avgHeart"
        tvHeartStatus.text = getHeartStatus(avgHeart)
        
        // Display heart rate pie chart
        val heartSlices = listOf(
            PieEntry(60f, "Steady"),
            PieEntry(20f, "Irregular"),
            PieEntry(20f, "Critical")
        )
        val heartSet = PieDataSet(heartSlices, "")
        heartSet.colors = listOf(
            Color.parseColor("#00C177"),
            Color.parseColor("#F9D438"),
            Color.parseColor("#FF4B4B")
        )
        heartSet.setDrawValues(false)
        pieHeart.data = PieData(heartSet)
        pieHeart.invalidate()
        pieHeart.animateY(600)
        
        // Steps
        val steps = analytics.averages.steps.toFloat()
        tvStepsStatus.text = getStepsStatus(steps)
        if (analytics.trends.steps.isNotEmpty()) {
            val stepsEntries = analytics.trends.steps.mapIndexed { i, trend ->
                BarEntry(i.toFloat(), trend.value.toFloat())
            }
            val stepsSet = BarDataSet(stepsEntries, "Steps")
            stepsSet.colors = stepsEntries.map { getColorForSteps(it.y) }
            barSteps.data = BarData(stepsSet)
            barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(currentTimeFilter))
            barSteps.invalidate()
            barSteps.animateY(700)
        }
        
        // Calories
        val calories = analytics.averages.calories.toFloat()
        tvCaloriesStatus.text = getCaloriesStatus(calories)
        val calEntries = listOf(BarEntry(0f, calories))
        val calSet = BarDataSet(calEntries, "Calories")
        calSet.colors = listOf(getColorForCalories(calories))
        barCalories.data = BarData(calSet)
        barCalories.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Avg"))
        barCalories.invalidate()
        
        // Sleep
        val sleep = analytics.averages.sleep.toFloat()
        tvSleepStatus.text = getSleepStatus(sleep)
        if (analytics.trends.sleep.isNotEmpty()) {
            val sleepEntries = analytics.trends.sleep.mapIndexed { i, trend ->
                BarEntry(i.toFloat(), trend.value.toFloat())
            }
            val sleepSet = BarDataSet(sleepEntries, "Sleep")
            sleepSet.colors = sleepEntries.map { getColorForSleep(it.y) }
            barSleep.data = BarData(sleepSet)
            barSleep.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(currentTimeFilter))
            barSleep.invalidate()
            barSleep.animateY(700)
        }
        
        // Blood Pressure (from heart rate trends as proxy)
        if (analytics.trends.heartRate.isNotEmpty()) {
            val bpEntries = analytics.trends.heartRate.mapIndexed { i, trend ->
                BarEntry(i.toFloat(), (trend.value * 1.6).toFloat()) // Convert HR to approximate BP
            }
            val bpSet = BarDataSet(bpEntries, "BP")
            bpSet.colors = listOf(Color.parseColor("#199EC8"))
            barBP.data = BarData(bpSet)
            barBP.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(currentTimeFilter))
            barBP.invalidate()
            barBP.animateY(700)
        }
        
        // Oxygen (sample data)
        val oxySlices = listOf(PieEntry(98f, "SpO2"), PieEntry(2f, "rest"))
        val oxySet = PieDataSet(oxySlices, "")
        oxySet.colors = listOf(Color.parseColor("#00C177"), Color.parseColor("#E6F7EE"))
        oxySet.setDrawValues(false)
        pieOxygen.data = PieData(oxySet)
        tvOxygenStatus.text = "Optimal"
        pieOxygen.invalidate()
    }
    
    private fun loadSampleData() {
        // Fallback to sample data if backend fails
        tvHeartValue.text = "75"
        tvHeartStatus.text = "Excellent"
        tvStepsStatus.text = "Active"
        tvCaloriesStatus.text = "Burning"
        tvSleepStatus.text = "Rested"
        tvOxygenStatus.text = "Optimal"
        
        // Load sample charts
        val heartSlices = listOf(PieEntry(60f, "Steady"), PieEntry(20f, "Irregular"), PieEntry(20f, "Critical"))
        val heartSet = PieDataSet(heartSlices, "")
        heartSet.colors = listOf(Color.parseColor("#00C177"), Color.parseColor("#F9D438"), Color.parseColor("#FF4B4B"))
        heartSet.setDrawValues(false)
        pieHeart.data = PieData(heartSet)
        pieHeart.invalidate()
        
        // Sample bar charts
        val sampleEntries = listOf(BarEntry(0f, 8500f))
        val sampleSet = BarDataSet(sampleEntries, "Sample")
        sampleSet.colors = listOf(Color.parseColor("#00C177"))
        barSteps.data = BarData(sampleSet)
        barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Today"))
        barSteps.invalidate()
    }

    private fun setupCardClicks() {
        cardHeart.setOnClickListener { showDetailDialog("Heart Rate", "heart") }
        cardBP.setOnClickListener { showDetailDialog("Blood Pressure", "bp") }
        cardSteps.setOnClickListener { showDetailDialog("Steps", "steps") }
        cardCalories.setOnClickListener { showDetailDialog("Calories", "calories") }
        cardSleep.setOnClickListener { showDetailDialog("Sleep Quality", "sleep") }
        cardOxygen.setOnClickListener { showDetailDialog("Oxygen Level", "oxygen") }
    }

    private fun showDetailDialog(title: String, type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_stat_detail)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Bind dialog views
        val dialogIcon = dialog.findViewById<ImageView>(R.id.dialogIcon)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogCurrentValue = dialog.findViewById<TextView>(R.id.dialogCurrentValue)
        val dialogUnit = dialog.findViewById<TextView>(R.id.dialogUnit)
        val dialogStatus = dialog.findViewById<TextView>(R.id.dialogStatus)
        val dialogAvgValue = dialog.findViewById<TextView>(R.id.dialogAvgValue)
        val dialogMinValue = dialog.findViewById<TextView>(R.id.dialogMinValue)
        val dialogMaxValue = dialog.findViewById<TextView>(R.id.dialogMaxValue)
        val dialogInsight = dialog.findViewById<TextView>(R.id.dialogInsight)
        val btnCloseDialog = dialog.findViewById<ImageView>(R.id.btnCloseDialog)
        val btnExportData = dialog.findViewById<CardView>(R.id.btnExportData)

        val dialogLineChart = dialog.findViewById<LineChart>(R.id.dialogLineChart)
        val dialogBarChart = dialog.findViewById<BarChart>(R.id.dialogBarChart)
        val dialogPieChart = dialog.findViewById<PieChart>(R.id.dialogPieChart)

        // Time filters
        val dialogBtnDaily = dialog.findViewById<Button>(R.id.dialogBtnDaily)
        val dialogBtnWeekly = dialog.findViewById<Button>(R.id.dialogBtnWeekly)
        val dialogBtnMonthly = dialog.findViewById<Button>(R.id.dialogBtnMonthly)
        val dialogBtnYearly = dialog.findViewById<Button>(R.id.dialogBtnYearly)

        // Set dialog title and icon
        dialogTitle.text = "$title Details"

        // Set data based on type
        when (type) {
            "heart" -> {
                dialogIcon.setImageResource(R.drawable.ic_heart)
                dialogIcon.setColorFilter(Color.parseColor("#FF4B4B"))
                dialogCurrentValue.text = "75"
                dialogUnit.text = "bpm"
                dialogStatus.text = "Excellent"
                dialogAvgValue.text = "72"
                dialogMinValue.text = "58"
                dialogMaxValue.text = "95"
                dialogInsight.text = "Your heart rate has been consistently in the healthy range. Keep up the good work with regular exercise!"
                
                // Show line chart
                dialogLineChart.visibility = View.VISIBLE
                dialogBarChart.visibility = View.GONE
                dialogPieChart.visibility = View.GONE
                setupLineChart(dialogLineChart)
                loadHeartLineData(dialogLineChart, "Daily")
            }
            "bp" -> {
                dialogIcon.setImageResource(R.drawable.ic_vitals)
                dialogIcon.setColorFilter(Color.parseColor("#1BA3C4"))
                dialogCurrentValue.text = "120/80"
                dialogUnit.text = "mmHg"
                dialogStatus.text = "Normal"
                dialogAvgValue.text = "118/78"
                dialogMinValue.text = "110/70"
                dialogMaxValue.text = "130/85"
                dialogInsight.text = "Your blood pressure is within normal range. Continue maintaining a healthy diet and exercise routine."
                
                dialogLineChart.visibility = View.GONE
                dialogBarChart.visibility = View.VISIBLE
                dialogPieChart.visibility = View.GONE
                setupBarChart(dialogBarChart)
                loadBPBarData(dialogBarChart, "Daily")
            }
            "steps" -> {
                dialogIcon.setImageResource(R.drawable.ic_steps)
                dialogIcon.setColorFilter(Color.parseColor("#1BA3C4"))
                dialogCurrentValue.text = "8,547"
                dialogUnit.text = "steps"
                dialogStatus.text = "Active"
                dialogAvgValue.text = "7,230"
                dialogMinValue.text = "3,200"
                dialogMaxValue.text = "12,450"
                dialogInsight.text = "Great job! You've been consistently hitting your step goals. Try adding some interval walking for extra benefits."
                
                dialogLineChart.visibility = View.GONE
                dialogBarChart.visibility = View.VISIBLE
                dialogPieChart.visibility = View.GONE
                setupBarChart(dialogBarChart)
                loadStepsBarData(dialogBarChart, "Daily")
            }
            "calories" -> {
                dialogIcon.setImageResource(R.drawable.ic_calories)
                dialogIcon.setColorFilter(Color.parseColor("#FF4B4B"))
                dialogCurrentValue.text = "1,847"
                dialogUnit.text = "kcal"
                dialogStatus.text = "Burning"
                dialogAvgValue.text = "1,650"
                dialogMinValue.text = "1,200"
                dialogMaxValue.text = "2,150"
                dialogInsight.text = "Your calorie burn is consistent with your activity level. Maintaining this pace will support your health goals."
                
                dialogLineChart.visibility = View.GONE
                dialogBarChart.visibility = View.VISIBLE
                dialogPieChart.visibility = View.GONE
                setupBarChart(dialogBarChart)
                loadCaloriesBarData(dialogBarChart, "Daily")
            }
            "sleep" -> {
                dialogIcon.setImageResource(R.drawable.ic_watch)
                dialogIcon.setColorFilter(Color.parseColor("#9C27B0"))
                dialogCurrentValue.text = "7.2"
                dialogUnit.text = "hours"
                dialogStatus.text = "Rested"
                dialogAvgValue.text = "6.8"
                dialogMinValue.text = "5.5"
                dialogMaxValue.text = "8.2"
                dialogInsight.text = "You're getting good quality sleep. Try to maintain a consistent bedtime to optimize your sleep patterns."
                
                dialogLineChart.visibility = View.GONE
                dialogBarChart.visibility = View.VISIBLE
                dialogPieChart.visibility = View.GONE
                setupBarChart(dialogBarChart)
                loadSleepBarData(dialogBarChart, "Daily")
            }
            "oxygen" -> {
                dialogIcon.setImageResource(R.drawable.ic_drop)
                dialogIcon.setColorFilter(Color.parseColor("#00BCD4"))
                dialogCurrentValue.text = "98"
                dialogUnit.text = "%"
                dialogStatus.text = "Optimal"
                dialogAvgValue.text = "97"
                dialogMinValue.text = "95"
                dialogMaxValue.text = "99"
                dialogInsight.text = "Your oxygen saturation levels are excellent. This indicates healthy respiratory and circulatory function."
                
                dialogLineChart.visibility = View.VISIBLE
                dialogBarChart.visibility = View.GONE
                dialogPieChart.visibility = View.GONE
                setupLineChart(dialogLineChart)
                loadOxygenLineData(dialogLineChart, "Daily")
            }
        }

        // Time filter buttons
        val setDialogFilter = { active: Button ->
            val all = listOf(dialogBtnDaily, dialogBtnWeekly, dialogBtnMonthly, dialogBtnYearly)
            all.forEach { b ->
                if (b == active) {
                    b.setBackgroundResource(R.drawable.filter_active)
                    b.setTextColor(Color.WHITE)
                } else {
                    b.setBackgroundResource(R.drawable.filter_inactive)
                    b.setTextColor(Color.parseColor("#527A89"))
                }
            }
        }

        dialogBtnDaily.setOnClickListener {
            setDialogFilter(dialogBtnDaily)
            when (type) {
                "heart" -> loadHeartLineData(dialogLineChart, "Daily")
                "bp" -> loadBPBarData(dialogBarChart, "Daily")
                "steps" -> loadStepsBarData(dialogBarChart, "Daily")
                "calories" -> loadCaloriesBarData(dialogBarChart, "Daily")
                "sleep" -> loadSleepBarData(dialogBarChart, "Daily")
                "oxygen" -> loadOxygenLineData(dialogLineChart, "Daily")
            }
        }

        dialogBtnWeekly.setOnClickListener {
            setDialogFilter(dialogBtnWeekly)
            when (type) {
                "heart" -> loadHeartLineData(dialogLineChart, "Weekly")
                "bp" -> loadBPBarData(dialogBarChart, "Weekly")
                "steps" -> loadStepsBarData(dialogBarChart, "Weekly")
                "calories" -> loadCaloriesBarData(dialogBarChart, "Weekly")
                "sleep" -> loadSleepBarData(dialogBarChart, "Weekly")
                "oxygen" -> loadOxygenLineData(dialogLineChart, "Weekly")
            }
        }

        dialogBtnMonthly.setOnClickListener {
            setDialogFilter(dialogBtnMonthly)
            when (type) {
                "heart" -> loadHeartLineData(dialogLineChart, "Monthly")
                "bp" -> loadBPBarData(dialogBarChart, "Monthly")
                "steps" -> loadStepsBarData(dialogBarChart, "Monthly")
                "calories" -> loadCaloriesBarData(dialogBarChart, "Monthly")
                "sleep" -> loadSleepBarData(dialogBarChart, "Monthly")
                "oxygen" -> loadOxygenLineData(dialogLineChart, "Monthly")
            }
        }

        dialogBtnYearly.setOnClickListener {
            setDialogFilter(dialogBtnYearly)
            when (type) {
                "heart" -> loadHeartLineData(dialogLineChart, "Yearly")
                "bp" -> loadBPBarData(dialogBarChart, "Yearly")
                "steps" -> loadStepsBarData(dialogBarChart, "Yearly")
                "calories" -> loadCaloriesBarData(dialogBarChart, "Yearly")
                "sleep" -> loadSleepBarData(dialogBarChart, "Yearly")
                "oxygen" -> loadOxygenLineData(dialogLineChart, "Yearly")
            }
        }

        btnCloseDialog.setOnClickListener { dialog.dismiss() }
        btnExportData.setOnClickListener {
            Toast.makeText(this, "Exporting $title data...", Toast.LENGTH_SHORT).show()
            // In production, implement CSV/PDF export
        }

        dialog.show()
    }

    private fun setupLineChart(chart: LineChart) {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.gridColor = Color.parseColor("#E0E0E0")
        chart.legend.isEnabled = false
        chart.setExtraOffsets(10f, 10f, 10f, 10f)
    }

    private fun loadHeartLineData(chart: LineChart, period: String) {
        val entries = when (period) {
            "Daily" -> listOf(
                Entry(0f, 70f), Entry(1f, 75f), Entry(2f, 72f), Entry(3f, 78f),
                Entry(4f, 73f), Entry(5f, 76f), Entry(6f, 75f)
            )
            "Weekly" -> listOf(
                Entry(0f, 72f), Entry(1f, 74f), Entry(2f, 73f), Entry(3f, 76f),
                Entry(4f, 75f), Entry(5f, 77f), Entry(6f, 74f)
            )
            "Monthly" -> listOf(
                Entry(0f, 73f), Entry(1f, 74f), Entry(2f, 75f), Entry(3f, 74f)
            )
            "Yearly" -> listOf(
                Entry(0f, 72f), Entry(1f, 73f), Entry(2f, 74f), Entry(3f, 75f),
                Entry(4f, 74f), Entry(5f, 75f), Entry(6f, 76f), Entry(7f, 75f),
                Entry(8f, 74f), Entry(9f, 75f), Entry(10f, 76f), Entry(11f, 75f)
            )
            else -> listOf()
        }

        val dataSet = LineDataSet(entries, "Heart Rate")
        dataSet.color = Color.parseColor("#FF4B4B")
        dataSet.lineWidth = 3f
        dataSet.setCircleColor(Color.parseColor("#FF4B4B"))
        dataSet.circleRadius = 5f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        chart.data = LineData(dataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateX(800)
    }

    private fun loadOxygenLineData(chart: LineChart, period: String) {
        val entries = when (period) {
            "Daily" -> listOf(
                Entry(0f, 97f), Entry(1f, 98f), Entry(2f, 97f), Entry(3f, 98f),
                Entry(4f, 99f), Entry(5f, 98f), Entry(6f, 98f)
            )
            "Weekly" -> listOf(
                Entry(0f, 97f), Entry(1f, 98f), Entry(2f, 97f), Entry(3f, 98f),
                Entry(4f, 98f), Entry(5f, 99f), Entry(6f, 98f)
            )
            "Monthly" -> listOf(
                Entry(0f, 97f), Entry(1f, 98f), Entry(2f, 98f), Entry(3f, 98f)
            )
            "Yearly" -> listOf(
                Entry(0f, 97f), Entry(1f, 97f), Entry(2f, 98f), Entry(3f, 98f),
                Entry(4f, 98f), Entry(5f, 98f), Entry(6f, 98f), Entry(7f, 99f),
                Entry(8f, 98f), Entry(9f, 98f), Entry(10f, 98f), Entry(11f, 98f)
            )
            else -> listOf()
        }

        val dataSet = LineDataSet(entries, "Oxygen")
        dataSet.color = Color.parseColor("#00BCD4")
        dataSet.lineWidth = 3f
        dataSet.setCircleColor(Color.parseColor("#00BCD4"))
        dataSet.circleRadius = 5f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        chart.data = LineData(dataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateX(800)
    }

    private fun loadBPBarData(chart: BarChart, period: String) {
        val values = when (period) {
            "Daily" -> listOf(120f, 118f, 122f, 119f, 121f, 120f, 118f)
            "Weekly" -> listOf(119f, 120f, 121f, 120f, 119f, 120f, 121f)
            "Monthly" -> listOf(120f, 119f, 121f, 120f)
            "Yearly" -> listOf(118f, 119f, 120f, 121f, 120f, 119f, 120f, 121f, 120f, 119f, 120f, 121f)
            else -> listOf()
        }

        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "BP")
        dataSet.color = Color.parseColor("#1BA3C4")
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        chart.data = data
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateY(800)
    }

    private fun loadStepsBarData(chart: BarChart, period: String) {
        val values = when (period) {
            "Daily" -> listOf(5400f, 7200f, 8500f, 9100f, 7800f, 8900f, 8547f)
            "Weekly" -> listOf(7200f, 7500f, 8100f, 8400f, 7900f, 8200f, 8547f)
            "Monthly" -> listOf(7500f, 7800f, 8100f, 8300f)
            "Yearly" -> listOf(6800f, 7100f, 7400f, 7700f, 7900f, 8100f, 8200f, 8400f, 8300f, 8200f, 8400f, 8547f)
            else -> listOf()
        }

        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "Steps")
        dataSet.colors = values.map { getColorForSteps(it) }
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        chart.data = data
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateY(800)
    }

    private fun loadCaloriesBarData(chart: BarChart, period: String) {
        val values = when (period) {
            "Daily" -> listOf(1500f, 1650f, 1750f, 1820f, 1690f, 1800f, 1847f)
            "Weekly" -> listOf(1600f, 1650f, 1700f, 1750f, 1720f, 1780f, 1847f)
            "Monthly" -> listOf(1650f, 1700f, 1750f, 1800f)
            "Yearly" -> listOf(1550f, 1600f, 1650f, 1700f, 1720f, 1750f, 1770f, 1800f, 1790f, 1810f, 1820f, 1847f)
            else -> listOf()
        }

        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "Calories")
        dataSet.colors = values.map { getColorForCalories(it) }
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        chart.data = data
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateY(800)
    }

    private fun loadSleepBarData(chart: BarChart, period: String) {
        val values = when (period) {
            "Daily" -> listOf(6.5f, 7.0f, 7.2f, 6.8f, 7.5f, 7.1f, 7.2f)
            "Weekly" -> listOf(6.8f, 7.0f, 7.1f, 7.2f, 7.0f, 7.3f, 7.2f)
            "Monthly" -> listOf(6.9f, 7.0f, 7.1f, 7.2f)
            "Yearly" -> listOf(6.7f, 6.8f, 6.9f, 7.0f, 7.1f, 7.0f, 7.1f, 7.2f, 7.1f, 7.2f, 7.2f, 7.2f)
            else -> listOf()
        }

        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "Sleep")
        dataSet.colors = values.map { getColorForSleep(it) }
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        chart.data = data
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(getLabelsForPeriod(period))
        chart.invalidate()
        chart.animateY(800)
    }

    private fun getLabelsForPeriod(period: String): List<String> {
        return when (period) {
            "day" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            "week" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            "month" -> listOf("W1", "W2", "W3", "W4")
            "year" -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            else -> listOf("Data")
        }
    }

    private fun setActiveFilter(active: Button) {
        val all = listOf(btnDaily, btnWeekly, btnMonthly, btnYearly)
        all.forEach { b ->
            if (b == active) {
                b.setBackgroundResource(R.drawable.filter_active)
                b.setTextColor(Color.WHITE)
            } else {
                b.setBackgroundResource(R.drawable.filter_inactive)
                b.setTextColor(Color.parseColor("#527A89"))
            }
        }
    }

    private fun setupCharts() {
        setupPieChart(pieHeart)
        setupPieChart(pieOxygen)
        setupBarChart(barBP)
        setupBarChart(barSteps)
        setupBarChart(barCalories)
        setupBarChart(barSleep)
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Set Tracking as active
        setNavigationActive(navTracking)

        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navTracking.setOnClickListener {
            // Already on Tracking
        }

        navAppt.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navAi.setOnClickListener {
            startActivity(Intent(this, AIAssistantActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun setNavigationActive(activeNav: LinearLayout) {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Reset all navigation items
        resetNavigationItem(navHome)
        resetNavigationItem(navTracking)
        resetNavigationItem(navAppt)
        resetNavigationItem(navOrders)
        resetNavigationItem(navAi)

        // Set active navigation item
        val iconBg = activeNav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = activeNav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(Color.parseColor("#E8F5F7"))
        iconBg.cardElevation = 2f
        icon.setColorFilter(Color.parseColor("#1BA3C4"))
        text.setTextColor(Color.parseColor("#1BA3C4"))
        text.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetNavigationItem(nav: LinearLayout) {
        val iconBg = nav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = nav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(Color.parseColor("#F5F8FA"))
        iconBg.cardElevation = 0f
        icon.setColorFilter(Color.parseColor("#6B7280"))
        text.setTextColor(Color.parseColor("#6B7280"))
        text.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    // common pie chart style
    private fun setupPieChart(pc: PieChart) {
        pc.description.isEnabled = false
        pc.isDrawHoleEnabled = true
        pc.holeRadius = 62f
        pc.setUsePercentValues(false)
        pc.setDrawEntryLabels(false)
        val l: Legend = pc.legend
        l.isEnabled = false
        pc.setExtraOffsets(10f, 10f, 10f, 10f)
    }

    // common bar chart style
    private fun setupBarChart(bc: BarChart) {
        bc.description.isEnabled = false
        bc.setDrawGridBackground(false)
        bc.setFitBars(true)
        bc.axisRight.isEnabled = false
        val x = bc.xAxis
        x.position = XAxis.XAxisPosition.BOTTOM
        x.setDrawGridLines(false)
        x.granularity = 1f
        bc.axisLeft.axisMinimum = 0f
        bc.legend.isEnabled = false
        bc.setExtraOffsets(8f, 8f, 8f, 8f)
    }

    // ------------------------
    // LOAD SAMPLE DATA
    // ------------------------
    private fun loadDailyData() {
        // --- Heart Rate donut (single value + distribution) ---
        val heartValue = 75 // sample
        tvHeartValue.text = "$heartValue"
        val heartStatus = getHeartStatus(heartValue)
        tvHeartStatus.text = heartStatus

        val heartSlices = listOf(
            PieEntry(60f, "Steady"), // green
            PieEntry(10f, "Irregular"), // yellow
            PieEntry(30f, "Critical") // red (visual only)
        )
        val heartSet = PieDataSet(heartSlices, "")
        heartSet.colors = listOf(Color.parseColor("#00C177"), Color.parseColor("#F9D438"), Color.parseColor("#FF4B4B"))
        heartSet.setDrawValues(false)
        val heartData = PieData(heartSet)
        pieHeart.data = heartData
        pieHeart.invalidate()
        pieHeart.animateY(600)

        // --- Blood Pressure bar (systolic or combined) ---
        val bpValues = listOf(120f, 125f, 118f, 130f, 135f, 128f, 122f)
        val bpEntries = bpValues.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val bpSet = BarDataSet(bpEntries, "BP")
        bpSet.colors = listOf(Color.parseColor("#199EC8"))
        val bpData = BarData(bpSet)
        bpData.barWidth = 0.6f
        barBP.data = bpData
        val bpLabels = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        barBP.xAxis.valueFormatter = IndexAxisValueFormatter(bpLabels)
        barBP.invalidate()
        barBP.animateY(700)

        // --- Steps chart ---
        val stepsToday = 5400f
        tvStepsStatus.text = getStepsStatus(stepsToday)
        val stepsEntries = listOf(BarEntry(0f, stepsToday))
        val stepsSet = BarDataSet(stepsEntries, "Steps")
        stepsSet.colors = listOf(getColorForSteps(stepsToday))
        barSteps.data = BarData(stepsSet)
        barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Today"))
        barSteps.invalidate()

        // --- Calories chart ---
        val cal = 1750f
        tvCaloriesStatus.text = getCaloriesStatus(cal)
        val calEntries = listOf(BarEntry(0f, cal))
        val calSet = BarDataSet(calEntries, "Calories")
        calSet.colors = listOf(getColorForCalories(cal))
        barCalories.data = BarData(calSet)
        barCalories.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Today"))
        barCalories.invalidate()

        // --- Sleep quality ---
        val sleep = 7.2f // hours or score
        tvSleepStatus.text = getSleepStatus(sleep)
        val sleepEntries = listOf(BarEntry(0f, sleep))
        val sleepSet = BarDataSet(sleepEntries, "Sleep")
        sleepSet.colors = listOf(getColorForSleep(sleep))
        barSleep.data = BarData(sleepSet)
        barSleep.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Last Night"))
        barSleep.invalidate()

        // --- Oxygen donut ---
        val spo2 = 98f
        tvOxygenStatus.text = getOxygenStatus(spo2)
        val oxySlices = listOf(PieEntry(spo2, "SpO2"), PieEntry(100f - spo2, "rest"))
        val oxySet = PieDataSet(oxySlices, "")
        oxySet.colors = listOf(Color.parseColor("#00C177"), Color.parseColor("#E6F7EE"))
        oxySet.setDrawValues(false)
        pieOxygen.data = PieData(oxySet)
        pieOxygen.invalidate()
    }

    private fun loadWeeklyData() {
        // sample weekly charts: bars for each day
        // Heart weekly change (for demo use bar chart on BP chart slot)
        val hrWeekly = listOf(70f, 75f, 80f, 78f, 85f, 76f, 77f)
        val hrEntries = hrWeekly.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val hrSet = BarDataSet(hrEntries, "HR")
        hrSet.colors = hrWeekly.map { v ->
            when {
                v > 120f -> Color.parseColor("#FF4B4B")
                v > 100f -> Color.parseColor("#F9D438")
                else -> Color.parseColor("#00C177")
            }
        }
        val hrData = BarData(hrSet)
        hrData.barWidth = 0.6f
        barBP.data = hrData
        val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        barBP.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        barBP.invalidate()
        barBP.animateY(700)

        // Steps weekly
        val stepsWeek = listOf(3000f, 4200f, 5600f, 7800f, 10200f, 8300f, 9000f)
        val stepsEntries = stepsWeek.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val stepsSet = BarDataSet(stepsEntries, "Steps")
        stepsSet.colors = stepsWeek.map { getColorForSteps(it) }
        barSteps.data = BarData(stepsSet)
        barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        barSteps.invalidate()

        // Calories weekly
        val calWeek = listOf(1500f, 1700f, 1650f, 2000f, 2100f, 1800f, 1750f)
        val calEntries = calWeek.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val calSet = BarDataSet(calEntries, "Calories")
        calSet.colors = calWeek.map { getColorForCalories(it) }
        barCalories.data = BarData(calSet)
        barCalories.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        barCalories.invalidate()

        // Sleep weekly (hours)
        val sleepWeek = listOf(6f, 7f, 7.5f, 8f, 6.2f, 5.5f, 7.3f)
        val sleepEntries = sleepWeek.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val sleepSet = BarDataSet(sleepEntries, "Sleep")
        sleepSet.colors = sleepWeek.map { getColorForSleep(it) }
        barSleep.data = BarData(sleepSet)
        barSleep.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        barSleep.invalidate()

        // Heart main value for display
        tvHeartValue.text = "78"
        tvHeartStatus.text = "Good"
    }

    private fun loadMonthlyData() {
        // Monthly aggregated data
        val weeks = listOf("W1", "W2", "W3", "W4")
        
        // BP monthly
        val bpMonth = listOf(118f, 120f, 122f, 119f)
        val bpEntries = bpMonth.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val bpSet = BarDataSet(bpEntries, "BP")
        bpSet.color = Color.parseColor("#199EC8")
        barBP.data = BarData(bpSet)
        barBP.xAxis.valueFormatter = IndexAxisValueFormatter(weeks)
        barBP.invalidate()

        // Steps monthly
        val stepsMonth = listOf(6500f, 7200f, 7800f, 8200f)
        val stepsEntries = stepsMonth.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val stepsSet = BarDataSet(stepsEntries, "Steps")
        stepsSet.colors = stepsMonth.map { getColorForSteps(it) }
        barSteps.data = BarData(stepsSet)
        barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(weeks)
        barSteps.invalidate()

        // Calories monthly
        val calMonth = listOf(1600f, 1700f, 1750f, 1800f)
        val calEntries = calMonth.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val calSet = BarDataSet(calEntries, "Calories")
        calSet.colors = calMonth.map { getColorForCalories(it) }
        barCalories.data = BarData(calSet)
        barCalories.xAxis.valueFormatter = IndexAxisValueFormatter(weeks)
        barCalories.invalidate()

        // Sleep monthly
        val sleepMonth = listOf(6.8f, 7.0f, 7.2f, 7.1f)
        val sleepEntries = sleepMonth.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val sleepSet = BarDataSet(sleepEntries, "Sleep")
        sleepSet.colors = sleepMonth.map { getColorForSleep(it) }
        barSleep.data = BarData(sleepSet)
        barSleep.xAxis.valueFormatter = IndexAxisValueFormatter(weeks)
        barSleep.invalidate()

        tvHeartValue.text = "76"
        tvHeartStatus.text = "Good"
    }

    private fun loadYearlyData() {
        // Yearly aggregated data
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        
        // BP yearly
        val bpYear = listOf(118f, 119f, 120f, 121f, 120f, 119f, 120f, 121f, 119f, 120f, 119f, 118f)
        val bpEntries = bpYear.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val bpSet = BarDataSet(bpEntries, "BP")
        bpSet.color = Color.parseColor("#199EC8")
        barBP.data = BarData(bpSet)
        barBP.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barBP.invalidate()

        // Steps yearly
        val stepsYear = listOf(6000f, 6500f, 7000f, 7200f, 7500f, 7800f, 8000f, 8200f, 8100f, 8300f, 8400f, 8500f)
        val stepsEntries = stepsYear.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val stepsSet = BarDataSet(stepsEntries, "Steps")
        stepsSet.colors = stepsYear.map { getColorForSteps(it) }
        barSteps.data = BarData(stepsSet)
        barSteps.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barSteps.invalidate()

        // Calories yearly
        val calYear = listOf(1500f, 1550f, 1600f, 1650f, 1670f, 1700f, 1720f, 1750f, 1760f, 1780f, 1800f, 1820f)
        val calEntries = calYear.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val calSet = BarDataSet(calEntries, "Calories")
        calSet.colors = calYear.map { getColorForCalories(it) }
        barCalories.data = BarData(calSet)
        barCalories.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barCalories.invalidate()

        // Sleep yearly
        val sleepYear = listOf(6.5f, 6.7f, 6.8f, 6.9f, 7.0f, 7.1f, 7.0f, 7.2f, 7.1f, 7.2f, 7.3f, 7.2f)
        val sleepEntries = sleepYear.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val sleepSet = BarDataSet(sleepEntries, "Sleep")
        sleepSet.colors = sleepYear.map { getColorForSleep(it) }
        barSleep.data = BarData(sleepSet)
        barSleep.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barSleep.invalidate()

        tvHeartValue.text = "75"
        tvHeartStatus.text = "Excellent"
    }

    // -------------------------
    // STATUS HELPERS (thresholds)
    // -------------------------
    private fun getHeartStatus(value: Int): String {
        return when {
            value > 120 -> "Critical"
            value > 100 -> "Irregular"
            else -> "Steady"
        }
    }

    private fun getStepsStatus(steps: Float): String {
        return when {
            steps >= 10000 -> "Active"
            steps >= 5000 -> "Low"
            else -> "Idle"
        }
    }

    private fun getColorForSteps(steps: Float): Int {
        return when {
            steps >= 10000 -> Color.parseColor("#00C177")
            steps >= 5000 -> Color.parseColor("#F9D438")
            else -> Color.parseColor("#FF4B4B")
        }
    }

    private fun getCaloriesStatus(cal: Float): String {
        return when {
            cal >= 2000 -> "Burning"
            cal >= 1200 -> "Slowing"
            else -> "Stalled"
        }
    }

    private fun getColorForCalories(cal: Float): Int {
        return when {
            cal >= 2000 -> Color.parseColor("#00C177")
            cal >= 1200 -> Color.parseColor("#F9D438")
            else -> Color.parseColor("#FF4B4B")
        }
    }

    private fun getSleepStatus(hours: Float): String {
        return when {
            hours >= 7.5f -> "Rested"
            hours >= 6f -> "Disturbed"
            else -> "Deprived"
        }
    }

    private fun getColorForSleep(hours: Float): Int {
        return when {
            hours >= 7.5f -> Color.parseColor("#00C177")
            hours >= 6f -> Color.parseColor("#F9D438")
            else -> Color.parseColor("#FF4B4B")
        }
    }

    private fun getOxygenStatus(spo2: Float): String {
        return when {
            spo2 >= 95f -> "Optimal"
            spo2 >= 90f -> "Low"
            else -> "Severe"
        }
    }

    // Other helpers...
    private fun getHeartColor(value: Float): Int {
        return if (value > 120) Color.parseColor("#FF4B4B") else if (value > 100) Color.parseColor("#F9D438") else Color.parseColor("#00C177")
    }
}
