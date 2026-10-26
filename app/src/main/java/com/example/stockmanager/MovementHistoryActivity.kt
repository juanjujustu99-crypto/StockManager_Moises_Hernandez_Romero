package com.example.stockmanager

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovementHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.movement_history_activity)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left + 20,
                systemBars.top + 20,
                systemBars.right + 20,
                systemBars.bottom + 20
            )

            insets
        }

        val txtHistory = findViewById<TextView>(R.id.txtHistory)

        val database = DatabaseProvider.getDatabase(this)

        val movementRepository = MovementRepository(database)

        val productRepository = ProductRepository(
            database.productDao()
        )

        lifecycleScope.launch {

            try {

                val movements =
                    movementRepository.getAllMovements()

                val products =
                    productRepository.getAllProducts()

                val productsById =
                    products.associateBy { it.id }

                if (movements.isEmpty()) {

                    txtHistory.setText(R.string.history_empty)


                } else {

                    val dateFormat = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    )

                    val history = movements.joinToString(
                        separator = "\n\n--------------------\n\n"
                    ) { movement ->

                        val product =
                            productsById[movement.productId]

                        val productName =
                            product?.name
                                ?: getString(R.string.history_product_unavailable)

                        val productCode =
                            product?.code
                                ?: getString(R.string.history_code_unavailable)

                        val movementDate = dateFormat.format(
                            Date(movement.date)
                        )

                        """
                        Producto: $productName
                        Código: $productCode
                        Tipo: ${movement.type}
                        Cantidad: ${movement.quantity}
                        Fecha: $movementDate
                        """.trimIndent()
                    }

                    txtHistory.text = history
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@MovementHistoryActivity,
                    getString(
                        R.string.history_error_message,
                        e.message ?: "Error desconocido"
                    ),
                    Toast.LENGTH_LONG
                ).show()

                txtHistory.setText(R.string.history_error)
            }
        }
    }
}