package com.example.stockmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    fun submitList(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName =
            itemView.findViewById<TextView>(R.id.tvProductName)

        private val tvCode =
            itemView.findViewById<TextView>(R.id.tvProductCode)

        private val tvCategory =
            itemView.findViewById<TextView>(R.id.tvProductCategory)

        private val tvDescription =
            itemView.findViewById<TextView>(R.id.tvProductDescription)

        private val tvPrice =
            itemView.findViewById<TextView>(R.id.tvProductPrice)

        private val tvQuantity =
            itemView.findViewById<TextView>(R.id.tvProductQuantity)

        private val tvMinimumStock =
            itemView.findViewById<TextView>(R.id.tvProductMinimumStock)

        private val tvSupplier =
            itemView.findViewById<TextView>(R.id.tvProductSupplier)

        private val btnEdit =
            itemView.findViewById<Button>(R.id.btnEditProduct)

        private val btnDelete =
            itemView.findViewById<Button>(R.id.btnDeleteProduct)

        fun bind(product: Product) {

            tvName.text = product.name
            tvCode.text = "Codigo: ${product.code}"

            tvCategory.text =
                "Categoria: ${product.category.ifBlank { "Sin categoria" }}"

            tvDescription.text =
                product.description

            tvDescription.visibility =
                if (product.description.isBlank()) View.GONE
                else View.VISIBLE

            val currency = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-MX")
            )

            tvPrice.text = "Precio: ${currency.format(product.price)}"

            tvQuantity.text =
                "Existencias: ${product.quantity}"

            tvMinimumStock.text =
                "Stock minimo: ${product.minimumStock}"

            tvSupplier.text =
                "Proveedor: ${product.supplier.ifBlank { "No especificado" }}"

            btnEdit.setOnClickListener {
                onEditClick(product)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }
}
