package com.example.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.core.data.local.entities.CartItem
import com.example.core.data.local.entities.CartItemWithProduct
import com.example.core.data.local.entities.Label
import com.example.core.data.local.entities.LabelProductCrossRef
import com.example.core.data.local.entities.LabelWithProducts
import com.example.core.data.local.entities.Order
import com.example.core.data.local.entities.OrderProductCrossRef
import com.example.core.data.local.entities.OrderWithProducts
import com.example.core.data.local.entities.Product
import com.example.core.data.local.entities.ProductWithLabels

@Dao
interface ProductDao {

    /**
     * INSERT PRODUK dengan beberapa LABEL
     * */
    @Transaction
    suspend fun placeProductWithLabels(product: Product, labelIds: List<Long>) {
        val productId = insertProduct(product)
        val crossRefs = labelIds.map { labelId -> LabelProductCrossRef(productId, labelId) }
        insertProductLabelCrossRef(crossRefs)
    }

    @Insert
    suspend fun insertProduct(product: Product): Long

    @Insert
    suspend fun insertProductLabelCrossRef(crossRef: List<LabelProductCrossRef>)

    /**
     * GET ALL PRODUCT
     * */
    @Query("SELECT * FROM products")
    fun getProductsWithLabels(): LiveData<List<ProductWithLabels>>

    /**
     * GET BY ID PRODUCT dengan beberapa LABEL
     * */
    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductWithLabelsById(productId: Long): LiveData<ProductWithLabels>

    /**
     * UPDATE STOCK PRODUCT
     * */
    @Query("UPDATE products SET stock = stock - :quantity WHERE productId = :productId")
    suspend fun reduceProductStock(productId: Long, quantity: Int)

    /**
     * DELETE PRODUCT
     * */
    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: Long)
}

@Dao
interface LabelDao {
    /**
     * INSERT LABEL
     * */
    @Insert
    suspend fun insertLabel(label: Label): Long

    /**
     * GET BY ID LABEL dengan beberapa PRODUCT
     * */
    @Transaction
    @Query("SELECT * FROM labels WHERE labelId = :labelId")
    fun getLabelWithProductsById(labelId: Long): LiveData<LabelWithProducts>
}

@Dao
interface CartItemDao {
    /**
     * INSERT CART ITEM
     * */
    @Insert
    suspend fun insertCartItem(cartItem: CartItem): Long

    /**
     * GET ALL CART ITEM dengan PRODUCT
     * */
    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getCartItemsWithProduct(): LiveData<List<CartItemWithProduct>>

    /**
     * GET BY ID CART ITEM dengan PRODUCT
     * */
    @Transaction
    @Query("SELECT * FROM cart_items WHERE cartItemId = :cartItemId")
    fun getCartItemWithProductById(cartItemId: Long): LiveData<CartItemWithProduct>

    /**
     * DELETE CART ITEM
     * */
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
}

@Dao
interface OrderDao {

    /**
     * INSERT ORDER dengan beberapa PRODUCT
     * */
    @Transaction
    suspend fun placeOrderWithProducts(order: Order, productIds: List<Long>) {
        val orderId = insertOrder(order)
        val crossRefs = productIds.map { productId -> OrderProductCrossRef(orderId, productId) }
        insertOrderProductCrossRefs(crossRefs)
    }

    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertOrderProductCrossRefs(crossRefs: List<OrderProductCrossRef>)

    /**
     * GET ALL ORDER dengan beberapa PRODUCT
     * */
    @Transaction
    @Query("SELECT * FROM orders")
    fun getOrdersWithProducts(): LiveData<List<OrderWithProducts>>

    /**
     * GET BY ID ORDER dengan PRODUCT
     * */
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrdersWithProductsById(orderId: Long): LiveData<OrderWithProducts>
}