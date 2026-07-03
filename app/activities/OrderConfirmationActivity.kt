package com.trendonstore.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.content.Intent

/**
 * شاشة تأكيد الطلب
 * Order Confirmation Screen
 */
class OrderConfirmationActivity : AppCompatActivity() {

    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)
        
        orderId = intent.getStringExtra("order_id") ?: ""
        
        setupViews()
    }

    private fun setupViews() {
        val orderIdText = findViewById<TextView>(R.id.order_id)
        orderIdText.text = "رقم الطلب: $orderId"

        val continueButton = findViewById<Button>(R.id.continue_button)
        continueButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        val trackButton = findViewById<Button>(R.id.track_button)
        trackButton.setOnClickListener {
            val intent = Intent(this, OrderTrackingActivity::class.java)
            intent.putExtra("order_id", orderId)
            startActivity(intent)
        }
    }
}
