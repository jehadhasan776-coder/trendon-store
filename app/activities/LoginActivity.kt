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
 * شاشة تسجيل الدخول
 * Login Screen
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: CognitoAuthManager
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        authManager = CognitoAuthManager(this)
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        signupText = findViewById(R.id.signup_text)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            
            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                emailInput.error = "الرجاء إدخال البريد الإلكتروني"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInput.error = "البريد الإلكتروني غير صحيح"
                false
            }
            password.isEmpty() -> {
                passwordInput.error = "الرجاء إدخال كلمة المرور"
                false
            }
            else -> true
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                loginButton.isEnabled = false
                showMessage("جاري تسجيل الدخول...")
                
                val result = authManager.signIn(email, password)
                result.onSuccess { signInResult ->
                    if (signInResult.isSignInComplete) {
                        showMessage("تم تسجيل الدخول بنجاح!")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        showMessage("تسجيل الدخول غير مكتمل")
                        loginButton.isEnabled = true
                    }
                }.onFailure { error ->
                    showMessage("فشل تسجيل الدخول: ${error.message}")
                    loginButton.isEnabled = true
                }
            } catch (e: Exception) {
                showMessage("خطأ: ${e.message}")
                loginButton.isEnabled = true
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
