package com.trendonstore.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import kotlinx.coroutines.launch
import com.trendonstore.aws.cognito.CognitoAuthManager

/**
 * شاشة تأكيد البريد الإلكتروني
 * Email Confirmation Screen
 */
class ConfirmEmailActivity : AppCompatActivity() {

    private lateinit var authManager: CognitoAuthManager
    private var email: String = ""
    private lateinit var codeInput: EditText
    private lateinit var confirmButton: Button
    private lateinit var emailDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)
        
        email = intent.getStringExtra("email") ?: ""
        authManager = CognitoAuthManager(this)
        
        initializeViews()
        setupListeners()
        displayEmail()
    }

    private fun initializeViews() {
        codeInput = findViewById(R.id.code_input)
        confirmButton = findViewById(R.id.confirm_button)
        emailDisplay = findViewById(R.id.email_display)
    }

    private fun setupListeners() {
        confirmButton.setOnClickListener {
            val code = codeInput.text.toString().trim()
            if (code.isEmpty()) {
                codeInput.error = "الرجاء إدخال الكود"
            } else {
                performConfirmation(code)
            }
        }
    }

    private fun displayEmail() {
        emailDisplay.text = "تم إرسال كود التحقق إلى: $email"
    }

    private fun performConfirmation(code: String) {
        lifecycleScope.launch {
            try {
                confirmButton.isEnabled = false
                showMessage("جاري التحقق...")
                
                val result = authManager.confirmSignUp(email, code)

                result.onSuccess {
                    showMessage("تم التحقق بنجاح! يمكنك الآن تسجيل الدخول")
                    startActivity(Intent(this@ConfirmEmailActivity, LoginActivity::class.java))
                    finishAffinity()
                }.onFailure { error ->
                    showMessage("فشل التحقق: ${error.message}")
                    confirmButton.isEnabled = true
                }
            } catch (e: Exception) {
                showMessage("خطأ: ${e.message}")
                confirmButton.isEnabled = true
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
