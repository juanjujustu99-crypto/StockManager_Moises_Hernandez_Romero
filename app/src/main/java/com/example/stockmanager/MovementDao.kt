package com.example.stockmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovementDao {

    @Insert
    suspend fun insertMovement(movement: Movement)

    @Query(
        """
        SELECT * FROM movements
        WHERE productId = :productId
        ORDER BY date DESC
        """
    )
    suspend fun getMovementsByProduct(
        productId: Int
    ): List<Movement>

    @Query(
        """
        SELECT * FROM movements
        ORDER BY date DESC
        """
    )
    suspend fun getAllMovements(): List<Movement>
}