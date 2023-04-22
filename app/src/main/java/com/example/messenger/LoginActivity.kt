package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val email_logout = findViewById<EditText>(R.id.email_edittext_logout).text.toString()
        val password_logout = findViewById<EditText>(R.id.password_edittext_logout).text.toString()
        val back_to_registration = findViewById<TextView>(R.id.back_to_registration_textView)

//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email_logout, password_logout)

        back_to_registration.setOnClickListener {
            finish()
        }

    }
}