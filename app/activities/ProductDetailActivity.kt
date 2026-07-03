package com.trendonstore.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import kotlinx.coroutines.launch
import com.trendonstore.aws.dynamodb.DynamoDBManager
import com.trendonstore.aws.dynamodb.Product
import com.squareup.picasso.Picasso

/**
 * شاشة تفاصيل المنتج
 * Product Detail Screen
 */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var dbManager: DynamoDBManager
    private var product: Product? = null
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    private lateinit var productRating: TextView
    private lateinit var productStock: TextView
    private lateinit var quantityInput: EditText
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        
        dbManager = DynamoDBManager(this)
        val productId = intent.getStringExtra("product_id") ?: return
        
        initializeViews()
        loadProduct(productId)
    }

    private fun initializeViews() {
        productImage = findViewById(R.id.product_image)
        productName = findViewById(R.id.product_name)
        productPrice = findViewById(R.id.product_price)
        productDescription = findViewById(R.id.product_description)
        productRating = findViewById(R.id.product_rating)
        productStock = findViewById(R.id.product_stock)
        quantityInput = findViewById(R.id.quantity_input)
        addButton = findViewById(R.id.add_to_cart_button)
    }

    private fun loadProduct(productId: String) {
        lifecycleScope.launch {
            try {
                val result = dbManager.getProduct(productId)
                result.onSuccess { loadedProduct ->
                    product = loadedProduct
                    displayProduct(loadedProduct)
                }.onFailure { error ->
                    showMessage("فشل تحميل المنتج: ${error.message}")
                    finish()
                }
            } catch (e: Exception) {
                showMessage("خطأ: ${e.message}")
                finish()
            }
        }
    }

    private fun displayProduct(product: Product) {
        Picasso.get()
            .load(product.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(productImage)

        productName.text = product.name
        productDescription.text = product.description
        productPrice.text = "${product.price} دينار أردني"
        productRating.text = "⭐ ${product.rating}/5 (${product.reviews} تقييم)"
        productStock.text = "المتوفر: ${product.stock} وحدة"

        addButton.setOnClickListener {
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 1
            addToCart(product, quantity)
        }
    }

    private fun addToCart(product: Product, quantity: Int) {
        when {
            quantity <= 0 -> {
                quantityInput.error = "الرجاء إدخال كمية صحيحة"
                showMessage("الكمية يجب أن تكون أكبر من صفر")
            }
            quantity > product.stock -> {
                quantityInput.error = "الكمية المطلوبة غير متوفرة"
                showMessage("الكمية المتوفرة: ${product.stock}")
            }
            else -> {
                showMessage("تمت إضافة $quantity من ${product.name} إلى السلة ✓")
                // حفظ في SharedPreferences أو قاعدة بيانات محلية
                finish()
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
