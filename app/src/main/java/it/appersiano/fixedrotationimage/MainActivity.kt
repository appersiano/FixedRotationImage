package it.appersiano.fixedrotationimage

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.atan


class MainActivity : AppCompatActivity(), SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private var gravSensorVals: FloatArray = FloatArray(10)


    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(event.values!!.clone(), gravSensorVals)
        }

        val x = gravSensorVals[0]
        val y = gravSensorVals[1]

        val xy = x / y
        val yx = y / x

        val degreeRotation = atan(xy)

        var rotation = Math.toDegrees(degreeRotation.toDouble())

        if (y < 0) {
            rotation += 180
        }

        tvzero.text = rotation.toInt().toString()
        tvYX.text = Math.toDegrees(atan(yx).toDouble()).toInt().toString()

        ivDroid.rotation = rotation.toFloat()
    }

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    val ALPHA = 0.15f
    protected fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input

        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
        return output
    }
}