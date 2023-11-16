package com.example.pract22_2v2_maksimov

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var logEditText: EditText
    private lateinit var backButton: Button
    private lateinit var passEditText: EditText
    private lateinit var logButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logEditText = findViewById(R.id.LoginEditText)
        passEditText = findViewById(R.id.PasswordEditText)
        logButton = findViewById(R.id.logButton)
        backButton = findViewById(R.id.BackButton)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        logButton.setOnClickListener {
            val login = logEditText.text.toString()
            val password = passEditText.text.toString()

            val storedEmail = sharedPreferences.getString("login", login)
            val storedHashedPassword = sharedPreferences.getString("password", password)

            if (login == storedEmail && password == storedHashedPassword) {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Неправильные данные", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}