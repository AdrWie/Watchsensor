package com.adde15w.watchsensor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.adde15w.watchsensor.helpers.MQTTHelper
import android.widget.TextView
import com.adde15w.watchsensor.helpers.ChartHelper
import com.adde15w.watchsensor.model.Telemetry
import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mikephil.charting.charts.LineChart
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var mqttHelper: MQTTHelper? = null
    var tempReceived: TextView? = null
    var humiReceived: TextView? = null

    lateinit var mChart: ChartHelper
    lateinit var chart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tempReceived = findViewById<TextView>(R.id.tempReceived)
        humiReceived = findViewById<TextView>(R.id.humiReceived)

        chart = findViewById(R.id.chart) as LineChart
        mChart = ChartHelper(chart)

        startMqtt()

    }

    private fun startMqtt() {
        mqttHelper = MQTTHelper(applicationContext)
        mqttHelper?.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val telemetry : Telemetry
                try {

                    val mapper = ObjectMapper()
                    telemetry   = mapper.readValue(message.toString(), Telemetry::class.java)

                    Log.w("DEBUG", telemetry.temperature.toString())
                    // Ändrade till lateinit på mChart
                    tempReceived?.text = telemetry.temperature.toString()
                    humiReceived?.text = telemetry.humidity.toString()
                    mChart.addEntry(telemetry.temperature!!, telemetry.humidity!!)

                } catch (e: JsonGenerationException) {
                    e.printStackTrace()
                } catch (e: JsonMappingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun connectionLost(cause: Throwable?) {

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
    }
}