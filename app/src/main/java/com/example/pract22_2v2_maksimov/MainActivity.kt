    package com.example.pract22_2v2_maksimov

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import androidx.appcompat.app.AppCompatActivity


    class MainActivity : AppCompatActivity() {
        private lateinit var loginButton: Button
        private lateinit var registerButton: Button

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            loginButton = findViewById(R.id.loginButton)
            registerButton = findViewById(R.id.registerButton)

            loginButton.setOnClickListener {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            registerButton.setOnClickListener {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }
    }