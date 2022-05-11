package com.miprimeraapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.*
import java.util.jar.Manifest
import android.os.Bundle
import android.view.View
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
//mensajes emergentes
import android.widget.Toast
//bye findviewby id
import kotlinx.android.synthetic.main.activity_main.*
//sms
import android.provider.Telephony
import android.telephony.SmsManager

//gps
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    //Variables globales (GET current location)
    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    //Variables globales (sms)

    //main
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //pregunta los permisos
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /*btGetLocation.setOnClickListener {
            getCurrentLocation()
        }*/
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS,android.Manifest.permission.SEND_SMS),111)
        }else{
            receiveMsg()
        }
        val btSendSMS: Button = findViewById(R.id.btSendSMS)
        btSendSMS.setOnClickListener {
            sendMsg()
            /*
            val etSMS : EditText = findViewById(R.id.etSMS)
            val etNumber : EditText = findViewById(R.id.etNumber)
            var sms : SmsManager = SmsManager.getDefault()
            sms.sendTextMessage(etNumber.text.toString(),"ME",etSMS.text.toString(),null,null)*/
        }
        btGetLocation.setOnClickListener {
            getCurrentLocation()
        }

    }
    //permisos sms
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            receiveMsg()
        }
    }*/
    //persmisos gps
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
            111 -> {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    receiveMsg()
            }
        }
    }
    //cuando recive sms
    private fun receiveMsg() {
        val etSMS : EditText = findViewById(R.id.etSMS)
        val etNumber : EditText = findViewById(R.id.etNumber)
        var br = object :BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
                    for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)){
                        etNumber.setText(sms.originatingAddress)
                        etSMS.setText(sms.displayMessageBody)
                    }
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
    //cuando envia un sms
    private fun sendMsg() {
        val etSMS : EditText = findViewById(R.id.etSMS)
        val etNumber : EditText = findViewById(R.id.etNumber)
        var sms : SmsManager = SmsManager.getDefault()
        sms.sendTextMessage(etNumber.text.toString(),"ME",etSMS.text.toString(),null,null)

    }
    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                latitude = location.latitude
                longitude = location.longitude

                tvLatitude.text = "Latitude: ${location.latitude}"
                tvLongitude.text = "Longitude: ${location.longitude}"
                tvProvider.text = "Provider: ${location.provider}"

                //btOpenMap.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }
}