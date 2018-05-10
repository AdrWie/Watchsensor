package com.adde15w.watchsensor.helpers

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions


class MQTTHelper {

    val serverUri = "tcp://m21.cloudmqtt.com:19920"
    val clientId = "MQTTAndroidClient"
    val subscriptionTopic = "sensor/+"
    val userName = "jtbsdmrd"
    val password = "os8C40X7G3Me"
    var mqttAndroidClient: MqttAndroidClient? = null


    constructor(context: Context) {

        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient?.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.w("MQTT", serverURI)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {

                Log.w("MQTT", message.toString())

            }

            override fun connectionLost(cause: Throwable?) {

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended) {
        mqttAndroidClient?.setCallback(callback)
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = userName
        mqttConnectOptions.password = password.toCharArray()

        try {

            mqttAndroidClient?.connect(mqttConnectOptions, null, object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient?.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w("MQTT", "Failed to connect to: " + serverUri + exception.toString())
                }

            })

        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun subscribeToTopic() {

        try {

            mqttAndroidClient?.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w("MQTT", "Subscribed.")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w("MQTT", "Subscribed failed.")
                }

            })

        } catch (ex: MqttException) {
            Log.w("MQTT", "Error: Subscription failed.")
            ex.printStackTrace()
        }
    }
}