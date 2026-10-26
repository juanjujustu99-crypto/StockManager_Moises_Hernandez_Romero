
package com.example.stockmanager

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProductManagementActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository
    private lateinit var movementRepository: MovementRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 32, 32, 32)

        val title = TextView(this)
        title.setText(R.string.management_title)
        title.textSize = 24f
        layout.addView(title)

        val description = TextView(this)
        description.setText(R.string.management_description)
        description.textSize = 16f
        layout.addView(description)

        setContentView(layout)

        val database = DatabaseProvider.getDatabase(this)

        productRepository = ProductRepository(database.productDao())
        movementRepository = MovementRepository(database)

        mostrarProductos()
    }

    private fun mostrarProductos() {
        lifecycleScope.launch {
            try {
                val products = productRepository.getAllProducts()

                if (products.isEmpty()) {
                    Toast.makeText(
                        this@ProductManagementActivity,
                        R.string.management_empty,
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@launch
                }

                val productNames = products.map { product ->
                    "${product.code} - ${product.name} " +
                            "(Cantidad: ${product.quantity})"
                }.toTypedArray()

                AlertDialog.Builder(this@ProductManagementActivity)
                    .setTitle(R.string.management_select_product)
                    .setItems(productNames) { _, position ->
                        val product = products[position]
                        mostrarOpciones(product)
                    }
                    .setNegativeButton(R.string.management_close) { _, _ ->
                        finish()
                    }
                    .setOnCancelListener {
                        finish()
                    }
                    .show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductManagementActivity,
                    getString(
                        R.string.management_query_error,
                        e.message ?: "Error desconocido"
                    ),
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }

    private fun mostrarOpciones(product: Product) {
        val opciones = arrayOf(
            getString(R.string.management_edit),
            getString(R.string.management_delete)
        )

        AlertDialog.Builder(this)
            .setTitle(product.name)
            .setItems(opciones) { _, position ->
                when (position) {
                    0 -> editarProducto(product)
                    1 -> confirmarEliminacion(product)
                }
            }
            .setNegativeButton(R.string.management_cancel, null)
            .show()
    }

    private fun editarProducto(product: Product) {
        val intent = android.content.Intent(
            this,
            RegisterProductActivity::class.java
        )

        intent.putExtra("product_id", product.id)
        intent.putExtra("product_code", product.code)
        intent.putExtra("product_name", product.name)

        startActivity(intent)
    }

    private fun confirmarEliminacion(product: Product) {
        AlertDialog.Builder(this)
            .setTitle(R.string.management_delete_title)
            .setMessage(
                getString(
                    R.string.management_delete_message,
                    product.name
                )
            )
            .setPositiveButton(R.string.management_delete) { _, _ ->
                eliminarProducto(product)
            }
            .setNegativeButton(R.string.management_cancel, null)
            .show()
    }

    private fun eliminarProducto(product: Product) {
        lifecycleScope.launch {
            try {
                val movimientos =
                    movementRepository.getMovementsByProduct(product.id)

                if (movimientos.isNotEmpty()) {
                    Toast.makeText(
                        this@ProductManagementActivity,
                        "No se puede eliminar este producto porque tiene movimientos registrados. Se conservará su historial.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                productRepository.deleteProduct(product)

                Toast.makeText(
                    this@ProductManagementActivity,
                    R.string.management_delete_success,
                    Toast.LENGTH_LONG
                ).show()

                mostrarProductos()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductManagementActivity,
                    getString(
                        R.string.management_delete_error,
                        e.message ?: "Error desconocido"
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
