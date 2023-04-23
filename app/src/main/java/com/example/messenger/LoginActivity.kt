package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val back_to_registration = findViewById<TextView>(R.id.back_to_registration_textView)
        val button_sign_logout = findViewById<Button>(R.id.button_sign_logout)

        button_sign_logout.setOnClickListener {
            performLogin()
        }


        back_to_registration.setOnClickListener {
            finish()
        }

    }
    private fun performLogin()
    {
        val email_logout = findViewById<EditText>(R.id.email_edittext_logout).text.toString()
        val password_logout = findViewById<EditText>(R.id.password_edittext_logout).text.toString()
        if(email_logout.isEmpty() || password_logout.isEmpty()) {
            Toast.makeText(this, "Please fill out email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email_logout, password_logout)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}