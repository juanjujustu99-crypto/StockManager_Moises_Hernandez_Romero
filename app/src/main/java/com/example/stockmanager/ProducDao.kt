package com.example.stockmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE code = :productCode LIMIT 1")
    suspend fun getProductByCode(productCode: String): Product?

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)
}