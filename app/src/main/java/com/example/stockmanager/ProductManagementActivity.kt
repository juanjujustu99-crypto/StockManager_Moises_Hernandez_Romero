
package com.example.stockmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ProductManagementActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository
    private lateinit var movementRepository: MovementRepository
    private lateinit var productAdapter: ProductAdapter

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var txtEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_management)

        val main = findViewById<View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(main) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                20 + systemBars.left,
                20 + systemBars.top,
                20 + systemBars.right,
                20 + systemBars.bottom
            )

            insets
        }

        // Conectar base de datos
        val database = DatabaseProvider.getDatabase(this)

        productRepository = ProductRepository(database.productDao())
        movementRepository = MovementRepository(database)

        // Configurar RecyclerView
        recyclerProducts =
            findViewById(R.id.recyclerProducts)

        txtEmpty = findViewById(R.id.txtEmpty)

        productAdapter = ProductAdapter(
            onEditClick = { product ->
                editarProducto(product)
            },
            onDeleteClick = { product ->
                confirmarEliminacion(product)
            }
        )

        recyclerProducts.layoutManager =
            LinearLayoutManager(this)

        recyclerProducts.adapter = productAdapter

        // Boton para registrar productos
        findViewById<Button>(R.id.btnAddProduct)
            .setOnClickListener {

                val intent = Intent(
                    this,
                    RegisterProductActivity::class.java
                )

                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()

        cargarProductos()
    }

    private fun cargarProductos() {

        lifecycleScope.launch {

            try {

                val products = productRepository.getAllProducts()

                productAdapter.submitList(products)

                if (products.isEmpty()) {

                    txtEmpty.visibility = View.VISIBLE
                    recyclerProducts.visibility = View.GONE

                } else {

                    txtEmpty.visibility = View.GONE
                    recyclerProducts.visibility = View.VISIBLE
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@ProductManagementActivity,
                    "Error al consultar productos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun editarProducto(product: Product) {

        val intent = Intent(
            this,
            RegisterProductActivity::class.java
        )

        intent.putExtra("product_id", product.id)

        startActivity(intent)
    }

    private fun confirmarEliminacion(product: Product) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage(
                "Estas seguro de eliminar ${product.name}?"
            )
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(product)
            }
            .setNegativeButton("Cancelar", null)
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
                        "No se puede eliminar este producto porque tiene movimientos registrados.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                productRepository.deleteProduct(product)

                Toast.makeText(
                    this@ProductManagementActivity,
                    "Producto eliminado correctamente.",
                    Toast.LENGTH_LONG
                ).show()

                cargarProductos()

            } catch (e: Exception) {

                Toast.makeText(
                    this@ProductManagementActivity,
                    "Error al eliminar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
