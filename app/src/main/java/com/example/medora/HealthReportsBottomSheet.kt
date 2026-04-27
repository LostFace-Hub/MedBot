package com.example.medora

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.network.HealthReport
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class HealthReportsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var tabDaily: TextView
    private lateinit var tabWeekly: TextView
    private lateinit var tabMonthly: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var reportContainer: LinearLayout
    private lateinit var tvNoData: TextView
    
    private var currentPeriod = "week"

    override fun getTheme(): Int = R.style.DialogStyle

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            dialog.window?.setDimAmount(0.7f)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_health_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        tabDaily = view.findViewById(R.id.tabDaily)
        tabWeekly = view.findViewById(R.id.tabWeekly)
        tabMonthly = view.findViewById(R.id.tabMonthly)
        progressBar = view.findViewById(R.id.progressBar)
        reportContainer = view.findViewById(R.id.reportContainer)
        tvNoData = view.findViewById(R.id.tvNoData)

        btnClose.setOnClickListener { dismiss() }

        tabDaily.setOnClickListener {
            currentPeriod = "day"
            updateTabSelection()
            fetchHealthReport()
        }

        tabWeekly.setOnClickListener {
            currentPeriod = "week"
            updateTabSelection()
            fetchHealthReport()
        }

        tabMonthly.setOnClickListener {
            currentPeriod = "month"
            updateTabSelection()
            fetchHealthReport()
        }

        updateTabSelection()
        fetchHealthReport()
    }

    private fun updateTabSelection() {
        tabDaily.setBackgroundResource(
            if (currentPeriod == "day") R.drawable.tab_active else R.drawable.tab_inactive
        )
        tabWeekly.setBackgroundResource(
            if (currentPeriod == "week") R.drawable.tab_active else R.drawable.tab_inactive
        )
        tabMonthly.setBackgroundResource(
            if (currentPeriod == "month") R.drawable.tab_active else R.drawable.tab_inactive
        )

        val activeColor = resources.getColor(R.color.white, null)
        val inactiveColor = resources.getColor(R.color.textSecondary, null)

        tabDaily.setTextColor(if (currentPeriod == "day") activeColor else inactiveColor)
        tabWeekly.setTextColor(if (currentPeriod == "week") activeColor else inactiveColor)
        tabMonthly.setTextColor(if (currentPeriod == "month") activeColor else inactiveColor)
    }

    private fun fetchHealthReport() {
        progressBar.visibility = View.VISIBLE
        reportContainer.visibility = View.GONE
        tvNoData.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getHealthReport(currentPeriod)
                Log.d("HealthReports", "Response: ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    val report = response.body()?.data
                    report?.let {
                        displayReport(it)
                    } ?: showNoData()
                } else {
                    Log.e("HealthReports", "API error: ${response.message()}")
                    showNoData()
                }
            } catch (e: Exception) {
                Log.e("HealthReports", "Error fetching report: ${e.message}", e)
                showNoData()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayReport(report: HealthReport) {
        reportContainer.removeAllViews()
        reportContainer.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(requireContext())

        // Summary Card
        val summaryView = inflater.inflate(R.layout.item_report_summary, reportContainer, false)
        val tvStatus = summaryView.findViewById<TextView>(R.id.tvStatus)
        val tvMessage = summaryView.findViewById<TextView>(R.id.tvMessage)
        val tvDataPoints = summaryView.findViewById<TextView>(R.id.tvDataPoints)

        tvStatus.text = report.summary.status
        tvMessage.text = report.summary.message
        tvDataPoints.text = "${report.dataPoints} data points"

        // Color code status
        val statusColor = when (report.summary.status.lowercase()) {
            "good" -> resources.getColor(R.color.success, null)
            "needs attention" -> resources.getColor(R.color.danger, null)
            else -> resources.getColor(R.color.accent, null)
        }
        tvStatus.setTextColor(statusColor)

        reportContainer.addView(summaryView)

        // Metrics Cards
        report.metrics.heartRate?.let {
            addMetricCard("Heart Rate", "${it.average} ${it.unit}", "Min: ${it.min}, Max: ${it.max}")
        }

        report.metrics.bloodPressure?.let {
            addMetricCard("Blood Pressure", it.average, "Systolic: ${it.systolic}, Diastolic: ${it.diastolic}")
        }

        report.metrics.steps?.let {
            addMetricCard("Steps", "${it.average} avg", "Total: ${it.total} ${it.unit}")
        }

        report.metrics.calories?.let {
            addMetricCard("Calories", "${it.average} avg", "Total: ${it.total} ${it.unit}")
        }

        report.metrics.sleep?.let {
            addMetricCard("Sleep", "${it.average} ${it.unit}", "Average per night")
        }

        report.metrics.weight?.let {
            addMetricCard("Weight", "${it.latest} ${it.unit}", "Average: ${it.average} ${it.unit}")
        }
    }

    private fun addMetricCard(title: String, value: String, subtitle: String) {
        val inflater = LayoutInflater.from(requireContext())
        val metricView = inflater.inflate(R.layout.item_report_metric, reportContainer, false)

        val tvTitle = metricView.findViewById<TextView>(R.id.tvMetricTitle)
        val tvValue = metricView.findViewById<TextView>(R.id.tvMetricValue)
        val tvSubtitle = metricView.findViewById<TextView>(R.id.tvMetricSubtitle)

        tvTitle.text = title
        tvValue.text = value
        tvSubtitle.text = subtitle

        reportContainer.addView(metricView)
    }

    private fun showNoData() {
        tvNoData.visibility = View.VISIBLE
        tvNoData.text = "No health data available for this period"
        reportContainer.visibility = View.GONE
    }
}
