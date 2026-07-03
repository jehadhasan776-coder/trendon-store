package com.trendonstore.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import android.widget.ProgressBar
import android.widget.TextView
import android.content.Intent
import kotlinx.coroutines.launch
import com.trendonstore.aws.dynamodb.DynamoDBManager
import com.trendonstore.app.adapters.ProductAdapter

/**
 * شاشة المتجر الرئيسية - عرض المنتجات
 * Main Store Screen - Products List
 */
class MainActivity : AppCompatActivity() {

    private lateinit var dbManager: DynamoDBManager
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        dbManager = DynamoDBManager(this)
        initializeViews()
        setupRecyclerView()
        loadProducts()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.products_recycler)
        progressBar = findViewById(R.id.progress_bar)
        emptyText = findViewById(R.id.empty_text)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }
        recyclerView.adapter = productAdapter
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = android.view.View.VISIBLE
                val result = dbManager.getAllProducts()
                result.onSuccess { products ->
                    progressBar.visibility = android.view.View.GONE
                    if (products.isEmpty()) {
                        emptyText.visibility = android.view.View.VISIBLE
                        recyclerView.visibility = android.view.View.GONE
                    } else {
                        emptyText.visibility = android.view.View.GONE
                        recyclerView.visibility = android.view.View.VISIBLE
                        productAdapter.updateProducts(products)
                        showMessage("تم تحميل ${products.size} منتج")
                    }
                }.onFailure { error ->
                    progressBar.visibility = android.view.View.GONE
                    emptyText.visibility = android.view.View.VISIBLE
                    emptyText.text = "فشل تحميل المنتجات: ${error.message}"
                    showMessage("خطأ: ${error.message}")
                }
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                showMessage("خطأ: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
