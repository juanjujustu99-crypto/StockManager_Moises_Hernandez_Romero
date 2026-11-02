package com.example.stockmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        // Campos del formulario
        val edtCode = findViewById<EditText>(R.id.edtCode)
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtQuantity = findViewById<EditText>(R.id.edtQuantity)
        val edtPrice = findViewById<EditText>(R.id.edtPrice)
        val edtDescription = findViewById<EditText>(R.id.edtDescription)
        val edtCategory = findViewById<EditText>(R.id.edtCategory)
        val edtMinimumStock = findViewById<EditText>(R.id.edtMinimumStock)
        val edtSupplier = findViewById<EditText>(R.id.edtSupplier)

        val btnSaveProduct =
            findViewById<Button>(R.id.btnSaveProduct)

        val txtProductDescription =
            findViewById<TextView>(R.id.txtProductDescription)

        // Comprobar si se esta editando un producto
        productId = intent.getIntExtra("product_id", -1)

        if (productId != -1) {

            title = "Modificar producto"
            txtProductDescription.text =
                "Actualiza la informacion del producto."

            edtQuantity.isEnabled = false
            btnSaveProduct.text = "Actualizar producto"

            lifecycleScope.launch {
                try {
                    val products = productRepository.getAllProducts()

                    val product = products.find {
                        it.id == productId
                    }

                    if (product == null) {
                        Toast.makeText(
                            this@RegisterProductActivity,
                            "No se encontro el producto.",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                        return@launch
                    }

                    edtCode.setText(product.code)
                    edtName.setText(product.name)
                    edtQuantity.setText(product.quantity.toString())
                    edtPrice.setText(product.price.toString())
                    edtDescription.setText(product.description)
                    edtCategory.setText(product.category)
                    edtMinimumStock.setText(
                        product.minimumStock.toString()
                    )
                    edtSupplier.setText(product.supplier)

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterProductActivity,
                        "Error al consultar el producto: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }
        }

        // Guardar o actualizar producto
        btnSaveProduct.setOnClickListener {

            val code = edtCode.text.toString().trim()
            val name = edtName.text.toString().trim()
            val quantityText = edtQuantity.text.toString().trim()
            val priceText = edtPrice.text.toString().trim()

            val description =
                edtDescription.text.toString().trim()

            val category =
                edtCategory.text.toString().trim()

            val minimumStockText =
                edtMinimumStock.text.toString().trim()

            val supplier =
                edtSupplier.text.toString().trim()

            // Validar campos obligatorios
            if (
                code.isEmpty() ||
                name.isEmpty() ||
                quantityText.isEmpty() ||
                priceText.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Completa los campos obligatorios.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()
            val price = priceText.toDoubleOrNull()

            val minimumStock = if (minimumStockText.isEmpty()) {
                0
            } else {
                minimumStockText.toIntOrNull()
            }

            if (quantity == null || quantity < 0) {
                Toast.makeText(
                    this,
                    "La cantidad debe ser un numero valido.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (price == null || price < 0 || !price.isFinite()) {
                Toast.makeText(
                    this,
                    "El precio debe ser un numero valido.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (minimumStock == null || minimumStock < 0) {
                Toast.makeText(
                    this,
                    "El stock minimo debe ser un numero valido.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val products = productRepository.getAllProducts()

                    // Evitar codigos duplicados
                    val existingProduct = products.find {
                        it.code.equals(code, ignoreCase = true)
                    }

                    if (
                        existingProduct != null &&
                        existingProduct.id != productId
                    ) {
                        Toast.makeText(
                            this@RegisterProductActivity,
                            "El codigo del producto ya existe.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    if (productId != -1) {

                        // Actualizar producto existente
                        val product = products.find {
                            it.id == productId
                        }

                        if (product == null) {
                            Toast.makeText(
                                this@RegisterProductActivity,
                                "No se encontro el producto.",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                            return@launch
                        }

                        val updatedProduct = product.copy(
                            code = code,
                            name = name,
                            price = price,
                            description = description,
                            category = category,
                            minimumStock = minimumStock,
                            supplier = supplier
                        )

                        productRepository.updateProduct(
                            updatedProduct
                        )

                        Toast.makeText(
                            this@RegisterProductActivity,
                            "Producto actualizado correctamente.",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        // Registrar producto nuevo
                        val product = Product(
                            code = code,
                            name = name,
                            quantity = quantity,
                            price = price,
                            description = description,
                            category = category,
                            minimumStock = minimumStock,
                            supplier = supplier
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
                        "Error al guardar el producto: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
