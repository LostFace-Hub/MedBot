package com.example.medora

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * Manager class for Health Connect API integration
 * Handles reading health data from connected wearable devices
 */
class HealthConnectManager(private val context: Context) {

    companion object {
        private const val TAG = "HealthConnectManager"
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
    }

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    // Define permissions needed for health data
    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class)
    )

    /**
     * Check if Health Connect is available on the device
     */
    suspend fun isAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                HealthConnectClient.isProviderAvailable(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking Health Connect availability", e)
                false
            }
        }
    }

    /**
     * Check if Health Connect app is installed
     */
    fun isHealthConnectInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(HEALTH_CONNECT_PACKAGE, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Open Play Store to install Health Connect
     */
    fun openHealthConnectPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$HEALTH_CONNECT_PACKAGE")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Check if all required permissions are granted
     */
    suspend fun hasAllPermissions(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val granted = healthConnectClient.permissionController.getGrantedPermissions()
                permissions.all { it in granted }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking permissions", e)
                false
            }
        }
    }

    /**
     * Create permission contract for requesting permissions
     */
    fun createPermissionContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    /**
     * Fetch heart rate data from the last 24 hours
     */
    suspend fun getHeartRateData(hoursBack: Long = 24): List<HeartRateData> {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(hoursBack, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                response.records.flatMap { record ->
                    record.samples.map { sample ->
                        HeartRateData(
                            bpm = sample.beatsPerMinute,
                            timestamp = sample.time.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading heart rate data", e)
                emptyList()
            }
        }
    }

    /**
     * Fetch steps data
     */
    suspend fun getStepsData(hoursBack: Long = 24): Long {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(hoursBack, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                response.records.sumOf { it.count }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading steps data", e)
                0L
            }
        }
    }

    /**
     * Fetch calories burned data
     */
    suspend fun getCaloriesData(hoursBack: Long = 24): Double {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(hoursBack, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = ActiveCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                response.records.sumOf { it.energy.inKilocalories }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading calories data", e)
                0.0
            }
        }
    }

    /**
     * Fetch sleep data
     */
    suspend fun getSleepData(daysBack: Long = 1): Double {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(daysBack * 24, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                val totalMinutes = response.records.sumOf { record ->
                    ChronoUnit.MINUTES.between(record.startTime, record.endTime)
                }
                totalMinutes / 60.0 // Convert to hours
            } catch (e: Exception) {
                Log.e(TAG, "Error reading sleep data", e)
                0.0
            }
        }
    }

    /**
     * Fetch oxygen saturation data
     */
    suspend fun getOxygenSaturationData(hoursBack: Long = 24): Double {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(hoursBack, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                if (response.records.isNotEmpty()) {
                    val avgPercentage = response.records.map { it.percentage.value }.average()
                    avgPercentage
                } else {
                    0.0
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading oxygen saturation data", e)
                0.0
            }
        }
    }

    /**
     * Fetch blood pressure data
     */
    suspend fun getBloodPressureData(hoursBack: Long = 24): BloodPressureData? {
        return withContext(Dispatchers.IO) {
            try {
                val endTime = Instant.now()
                val startTime = endTime.minus(hoursBack, ChronoUnit.HOURS)

                val request = ReadRecordsRequest(
                    recordType = BloodPressureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )

                val response = healthConnectClient.readRecords(request)
                if (response.records.isNotEmpty()) {
                    val latest = response.records.first()
                    BloodPressureData(
                        systolic = latest.systolic.inMillimetersOfMercury,
                        diastolic = latest.diastolic.inMillimetersOfMercury,
                        timestamp = latest.time.toString()
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading blood pressure data", e)
                null
            }
        }
    }

    /**
     * Sync all health data and return aggregated result
     */
    suspend fun syncAllHealthData(): HealthDataSync {
        return withContext(Dispatchers.IO) {
            try {
                val heartRateList = getHeartRateData()
                val avgHeartRate = if (heartRateList.isNotEmpty()) {
                    heartRateList.map { it.bpm }.average().toInt()
                } else 0

                HealthDataSync(
                    heartRate = avgHeartRate,
                    steps = getStepsData().toInt(),
                    calories = getCaloriesData().toInt(),
                    sleepHours = getSleepData(),
                    oxygenSaturation = getOxygenSaturationData().toInt(),
                    bloodPressure = getBloodPressureData(),
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing health data", e)
                HealthDataSync()
            }
        }
    }
}

// Data classes for health data
data class HeartRateData(
    val bpm: Long,
    val timestamp: String
)

data class BloodPressureData(
    val systolic: Double,
    val diastolic: Double,
    val timestamp: String
)

data class HealthDataSync(
    val heartRate: Int = 0,
    val steps: Int = 0,
    val calories: Int = 0,
    val sleepHours: Double = 0.0,
    val oxygenSaturation: Int = 0,
    val bloodPressure: BloodPressureData? = null,
    val timestamp: Long = System.currentTimeMillis()
)
