package com.example.stockmanager

class ProductRepository(
    private val productDao: ProductDao
) {

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun getProductByCode(code: String): Product? {
        return productDao.getProductByCode(code)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
}