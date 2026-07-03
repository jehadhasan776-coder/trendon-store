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
 * شاشة التسجيل (إنشاء حساب جديد)
 * Signup Screen
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var authManager: CognitoAuthManager
    private lateinit var fullNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        
        authManager = CognitoAuthManager(this)
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        fullNameInput = findViewById(R.id.fullname_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        passwordInput = findViewById(R.id.password_input)
        signupButton = findViewById(R.id.signup_button)
        loginText = findViewById(R.id.login_text)
    }

    private fun setupListeners() {
        signupButton.setOnClickListener {
            val fullName = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString()
            
            if (validateInput(fullName, email, phone, password)) {
                performSignup(fullName, email, phone, password)
            }
        }

        loginText.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {
        return when {
            fullName.isEmpty() -> {
                fullNameInput.error = "الرجاء إدخال الاسم الكامل"
                false
            }
            email.isEmpty() -> {
                emailInput.error = "الرجاء إدخال البريد الإلكتروني"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInput.error = "البريد الإلكتروني غير صحيح"
                false
            }
            phone.isEmpty() -> {
                phoneInput.error = "الرجاء إدخال رقم الهاتف"
                false
            }
            !phone.startsWith("+962") -> {
                phoneInput.error = "رقم الهاتف يجب أن يبدأ بـ +962"
                false
            }
            password.isEmpty() -> {
                passwordInput.error = "الرجاء إدخال كلمة المرور"
                false
            }
            password.length < 8 -> {
                passwordInput.error = "كلمة المرور يجب أن تكون 8 أحرف على الأقل"
                false
            }
            else -> true
        }
    }

    private fun performSignup(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ) {
        lifecycleScope.launch {
            try {
                signupButton.isEnabled = false
                showMessage("جاري إنشاء الحساب...")
                
                val result = authManager.signUp(
                    email = email,
                    password = password,
                    phoneNumber = phone,
                    fullName = fullName
                )

                result.onSuccess { signUpResult ->
                    showMessage("تم التسجيل بنجاح! الرجاء تأكيد البريد الإلكتروني")
                    val intent = Intent(this@SignupActivity, ConfirmEmailActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                }.onFailure { error ->
                    showMessage("فشل التسجيل: ${error.message}")
                    signupButton.isEnabled = true
                }
            } catch (e: Exception) {
                showMessage("خطأ: ${e.message}")
                signupButton.isEnabled = true
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
