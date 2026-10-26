package com.example.stockmanager

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Botón para consultar y administrar productos
        val btnProducts = findViewById<Button>(R.id.btnProducts)

        btnProducts.setOnClickListener {
            val intent = android.content.Intent(
                this,
                ProductManagementActivity::class.java
            )

            startActivity(intent)
        }

        // Botón para registrar productos
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val intent = android.content.Intent(
                this,
                RegisterProductActivity::class.java
            )

            startActivity(intent)
        }

        // Botón para registrar movimientos
        val btnMovement = findViewById<Button>(R.id.btnMovement)

        btnMovement.setOnClickListener {
            val intent = android.content.Intent(
                this,
                MovementActivity::class.java
            )

            startActivity(intent)
        }

        // Botón para consultar historial de movimientos
        val btnHistory = findViewById<Button>(R.id.btnHistory)

        btnHistory.setOnClickListener {
            val intent = android.content.Intent(
                this,
                MovementHistoryActivity::class.java
            )

            startActivity(intent)
        }
    }
}
