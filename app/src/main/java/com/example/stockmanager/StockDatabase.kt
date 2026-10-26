
package com.example.stockmanager

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Product::class,
        Movement::class
    ],
    version = 3,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun movementDao(): MovementDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS movements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        synced INTEGER NOT NULL,
                        FOREIGN KEY(productId)
                        REFERENCES products(id)
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_movements_productId
                    ON movements(productId)
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {
                db.execSQL(
                    """
                    CREATE TABLE movements_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        synced INTEGER NOT NULL,
                        FOREIGN KEY(productId)
                        REFERENCES products(id)
                        ON DELETE NO ACTION
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO movements_new (
                        id, productId, type, quantity, date, synced
                    )
                    SELECT id, productId, type, quantity, date, synced
                    FROM movements
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE movements")

                db.execSQL(
                    "ALTER TABLE movements_new RENAME TO movements"
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_movements_productId
                    ON movements(productId)
                    """.trimIndent()
                )
            }
        }
    }
}
