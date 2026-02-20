package com.dominik.control.kidshield.data.core.providers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import com.dominik.control.kidshield.data.repository.SensorInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SensorProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SensorInfoRepository
){
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val sigMotionSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)

    // Listener for steps
    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val stepsSinceBoot = event.values[0].toLong()
                scope.launch {
                    repository.saveStepCount(stepsSinceBoot, System.currentTimeMillis())
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    // Trigger for Significant Motion
    private val sigMotionListener = object : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent) {
            scope.launch {
                repository.saveSignificantMotion(System.currentTimeMillis())
            }
            reRegisterSigMotion()
        }
    }

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_NORMAL, 60_000_000)
        }

        reRegisterSigMotion()

    }

    fun stop() {
        sensorManager.unregisterListener(stepListener)
        sigMotionSensor?.let {
            sensorManager.cancelTriggerSensor(sigMotionListener, it)
        }
    }

    private fun reRegisterSigMotion() {
        sigMotionSensor?.let {
            sensorManager.requestTriggerSensor(sigMotionListener, it)
        }
    }
}
