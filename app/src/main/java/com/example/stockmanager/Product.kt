package com.example.stockmanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: String,
    val name: String,
    val quantity: Int,

    @ColumnInfo(defaultValue = "0.0")
    val price: Double = 0.0,

    @ColumnInfo(defaultValue = "''")
    val description: String = "",

    @ColumnInfo(defaultValue = "''")
    val category: String = "",

    @ColumnInfo(defaultValue = "0")
    val minimumStock: Int = 0,

    @ColumnInfo(defaultValue = "''")
    val supplier: String = ""
)
