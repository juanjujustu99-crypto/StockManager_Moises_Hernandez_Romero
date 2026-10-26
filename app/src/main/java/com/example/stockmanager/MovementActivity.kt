package com.example.stockmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MovementActivity : AppCompatActivity() {

    private lateinit var movementRepository: MovementRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_movement)

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

        val database = DatabaseProvider.getDatabase(this)

        movementRepository = MovementRepository(database)

        val edtMovementCode =
            findViewById<EditText>(R.id.edtMovementCode)

        val edtMovementQuantity =
            findViewById<EditText>(R.id.edtMovementQuantity)

        val radioMovementType =
            findViewById<RadioGroup>(R.id.radioMovementType)

        val btnSaveMovement =
            findViewById<Button>(R.id.btnSaveMovement)

        btnSaveMovement.setOnClickListener {

            val productCode =
                edtMovementCode.text.toString().trim()

            val quantityText =
                edtMovementQuantity.text.toString().trim()

            if (productCode.isEmpty() || quantityText.isEmpty()) {

                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()

            if (quantity == null || quantity <= 0) {

                Toast.makeText(
                    this,
                    "Ingresa una cantidad válida mayor a cero",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val movementType = when (
                radioMovementType.checkedRadioButtonId
            ) {

                R.id.radioEntry -> "ENTRADA"

                R.id.radioExit -> "SALIDA"

                else -> null
            }

            if (movementType == null) {

                Toast.makeText(
                    this,
                    "Selecciona entrada o salida",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val result =
                        movementRepository.registerMovement(
                            productCode,
                            movementType,
                            quantity
                        )

                    Toast.makeText(
                        this@MovementActivity,
                        result,
                        Toast.LENGTH_LONG
                    ).show()

                    if (result == "Movimiento registrado correctamente") {
                        finish()
                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        this@MovementActivity,
                        "Error al registrar movimiento: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}