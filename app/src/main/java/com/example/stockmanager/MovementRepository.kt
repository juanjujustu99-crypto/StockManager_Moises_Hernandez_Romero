package com.example.stockmanager

import androidx.room.withTransaction

class MovementRepository(
    private val database: StockDatabase
) {

    private val movementDao = database.movementDao()
    private val productDao = database.productDao()

    suspend fun registerMovement(
        productCode: String,
        type: String,
        quantity: Int
    ): String {

        return database.withTransaction {

            if (quantity <= 0) {
                return@withTransaction "La cantidad debe ser mayor a cero"
            }

            if (type != "ENTRADA" && type != "SALIDA") {
                return@withTransaction "Tipo de movimiento inválido"
            }

            val product = productDao.getProductByCode(productCode)

            if (product == null) {
                return@withTransaction "Producto no encontrado"
            }

            val newQuantity = if (type == "ENTRADA") {
                product.quantity + quantity
            } else {
                product.quantity - quantity
            }

            if (newQuantity < 0) {
                return@withTransaction "No hay suficiente inventario"
            }

            productDao.updateProduct(
                product.copy(quantity = newQuantity)
            )

            movementDao.insertMovement(
                Movement(
                    productId = product.id,
                    type = type,
                    quantity = quantity
                )
            )

            "Movimiento registrado correctamente"
        }
    }

    suspend fun getMovementsByProduct(
        productId: Int
    ): List<Movement> {

        return movementDao.getMovementsByProduct(productId)
    }

    suspend fun getAllMovements(): List<Movement> {

        return movementDao.getAllMovements()
    }
}