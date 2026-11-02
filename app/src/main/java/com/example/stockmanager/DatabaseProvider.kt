
package com.example.stockmanager

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: StockDatabase? = null

    fun getDatabase(context: Context): StockDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                StockDatabase::class.java,
                "stock_manager_database"
            )
                .addMigrations(
                    StockDatabase.MIGRATION_1_2,
                    StockDatabase.MIGRATION_2_3,
                    StockDatabase.MIGRATION_3_4
                )
                .build()

            INSTANCE = instance

            instance
        }
    }
}
