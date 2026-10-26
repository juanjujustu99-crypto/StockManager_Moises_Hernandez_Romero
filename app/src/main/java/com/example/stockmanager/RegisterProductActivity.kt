package com.example.stockmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterProductActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository

    private var productId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_register_product)

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

        productRepository = ProductRepository(
            database.productDao()
        )

        val edtCode = findViewById<EditText>(R.id.edtCode)
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtQuantity = findViewById<EditText>(R.id.edtQuantity)
        val btnSaveProduct = findViewById<Button>(R.id.btnSaveProduct)

        val txtProductDescription =
            findViewById<android.widget.TextView>(R.id.txtProductDescription)

        // Comprobar si estamos editando un producto
        productId = intent.getIntExtra("product_id", -1)

        if (productId != -1) {

            title = getString(R.string.edit_product_title)
            txtProductDescription.setText(R.string.edit_product_description)


            edtCode.setText(
                intent.getStringExtra("product_code") ?: ""
            )

            edtName.setText(
                intent.getStringExtra("product_name") ?: ""
            )

            // La cantidad se conserva y no puede modificarse aquí
            edtQuantity.isEnabled = false

            btnSaveProduct.setText(R.string.update_product)

            // Consultar la cantidad real guardada
            lifecycleScope.launch {
                try {
                    val products = productRepository.getAllProducts()

                    val product = products.find {
                        it.id == productId
                    }

                    if (product == null) {

                        Toast.makeText(
                            this@RegisterProductActivity,
                            R.string.edit_product_not_found,
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                        return@launch
                    }

                    edtQuantity.setText(product.quantity.toString())

                } catch (e: Exception) {

                    Toast.makeText(
                        this@RegisterProductActivity,
                        getString(
                            R.string.edit_product_error,
                            e.message ?: "Error desconocido"
                        ),
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }
        }

        btnSaveProduct.setOnClickListener {

            val code = edtCode.text.toString().trim()
            val name = edtName.text.toString().trim()
            val quantityText = edtQuantity.text.toString().trim()

            if (code.isEmpty() || name.isEmpty() || quantityText.isEmpty()) {

                Toast.makeText(
                    this,
                    "Completa todos los campos.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()

            if (quantity == null || quantity < 0) {

                Toast.makeText(
                    this,
                    "La cantidad debe ser un número válido.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val products = productRepository.getAllProducts()

                    // Comprobar si el código ya pertenece a otro producto
                    val existingProduct = products.find {
                        it.code.equals(code, ignoreCase = true)
                    }

                    if (
                        existingProduct != null &&
                        existingProduct.id != productId
                    ) {

                        val errorMessage = if (productId != -1) {
                            getString(R.string.edit_product_code_exists)
                        } else {
                            "El código del producto ya existe."
                        }

                        Toast.makeText(
                            this@RegisterProductActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    if (productId != -1) {

                        // EDITAR PRODUCTO
                        val product = products.find {
                            it.id == productId
                        }

                        if (product == null) {

                            Toast.makeText(
                                this@RegisterProductActivity,
                                R.string.edit_product_not_found,
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                            return@launch
                        }

                        // Conservar la cantidad actual
                        val updatedProduct = product.copy(
                            code = code,
                            name = name
                        )

                        productRepository.updateProduct(
                            updatedProduct
                        )

                        Toast.makeText(
                            this@RegisterProductActivity,
                            R.string.edit_product_success,
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        // REGISTRAR PRODUCTO NUEVO
                        val product = Product(
                            code = code,
                            name = name,
                            quantity = quantity
                        )

                        productRepository.insertProduct(product)

                        Toast.makeText(
                            this@RegisterProductActivity,
                            "Producto registrado correctamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    finish()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@RegisterProductActivity,
                        getString(
                            R.string.edit_product_error,
                            e.message ?: "Error desconocido"
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
