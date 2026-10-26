package com.example.stockmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: String,
    val name: String,
    val quantity: Int
)