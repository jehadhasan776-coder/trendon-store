package com.trendonstore.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import android.content.Intent
import kotlinx.coroutines.launch
import com.trendonstore.aws.dynamodb.*

/**
 * شاشة سلة التسوق والدفع
 * Shopping Cart and Checkout Screen
 */
class CartActivity : AppCompatActivity() {

    private lateinit var dbManager: DynamoDBManager
    private var cartItems: MutableList<OrderItem> = mutableListOf()
    private var userId: String = ""
    private lateinit var governorateSpinner: Spinner
    private lateinit var addressInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var discountCodeInput: EditText
    private lateinit var totalPriceText: TextView
    private lateinit var shippingCostText: TextView
    private lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        dbManager = DynamoDBManager(this)
        userId = intent.getStringExtra("user_id") ?: ""
        
        initializeViews()
        setupGovernorateSpinner()
        setupCheckoutListener()
    }

    private fun initializeViews() {
        governorateSpinner = findViewById(R.id.governorate_spinner)
        addressInput = findViewById(R.id.address_input)
        phoneInput = findViewById(R.id.phone_input)
        discountCodeInput = findViewById(R.id.discount_code_input)
        totalPriceText = findViewById(R.id.total_price)
        shippingCostText = findViewById(R.id.shipping_cost)
        checkoutButton = findViewById(R.id.checkout_button)
    }

    private fun setupGovernorateSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            JORDANIAN_GOVERNORATES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        governorateSpinner.adapter = adapter
    }

    private fun setupCheckoutListener() {
        governorateSpinner.onItemSelectedListener = 
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    updateShippingCost()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

        checkoutButton.setOnClickListener {
            performCheckout()
        }
    }

    private fun updateShippingCost() {
        val governorate = governorateSpinner.selectedItem.toString()
        val shippingCost = SHIPPING_COST_BY_GOVERNORATE[governorate] ?: 5.0
        shippingCostText.text = "رسوم التوصيل: $shippingCost د.ا"
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val cartTotal = cartItems.sumOf { it.price * it.quantity }
        val governorate = governorateSpinner.selectedItem.toString()
        val shippingCost = SHIPPING_COST_BY_GOVERNORATE[governorate] ?: 5.0
        val total = cartTotal + shippingCost
        totalPriceText.text = "الإجمالي: $total د.ا"
    }

    private fun performCheckout() {
        val address = addressInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val governorate = governorateSpinner.selectedItem.toString()
        val discountCode = discountCodeInput.text.toString().trim()

        if (address.isEmpty() || phone.isEmpty()) {
            showMessage("الرجاء ملء جميع الحقول المطلوبة")
            return
        }

        if (!phone.startsWith("+962")) {
            phoneInput.error = "الرجاء إدخال رقم أردني (+962...)"
            return
        }

        lifecycleScope.launch {
            try {
                checkoutButton.isEnabled = false
                showMessage("جاري معالجة الطلب...")
                
                val totalPrice = cartItems.sumOf { it.price * it.quantity }
                val shippingCost = SHIPPING_COST_BY_GOVERNORATE[governorate] ?: 5.0

                val order = Order(
                    userId = userId,
                    products = cartItems,
                    totalPrice = totalPrice + shippingCost,
                    status = "معلق",
                    shippingAddress = address,
                    phoneNumber = phone,
                    governorate = governorate,
                    paymentMethod = "COD",
                    discountCode = discountCode.ifEmpty { null },
                    shippingCost = shippingCost
                )

                val result = dbManager.createOrder(order)
                result.onSuccess { createdOrder ->
                    showMessage("✓ تم إنشاء الطلب بنجاح!\nرقم الطلب: ${createdOrder.id}")
                    val intent = Intent(this@CartActivity, OrderConfirmationActivity::class.java)
                    intent.putExtra("order_id", createdOrder.id)
                    startActivity(intent)
                    finish()
                }.onFailure { error ->
                    showMessage("فشل إنشاء الطلب: ${error.message}")
                    checkoutButton.isEnabled = true
                }
            } catch (e: Exception) {
                showMessage("خطأ: ${e.message}")
                checkoutButton.isEnabled = true
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
