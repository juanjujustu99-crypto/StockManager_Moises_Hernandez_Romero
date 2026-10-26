package com.example.stockmanager

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movements",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["productId"])
    ]
)
data class Movement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val productId: Int,

    val type: String,

    val quantity: Int,

    val date: Long = System.currentTimeMillis(),

    val synced: Boolean = false
)