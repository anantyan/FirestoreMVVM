package com.example.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.data.local.dao.CartItemDao
import com.example.core.data.local.dao.LabelDao
import com.example.core.data.local.dao.OrderDao
import com.example.core.data.local.dao.ProductDao
import com.example.core.data.local.database.NoteDatabase
import com.example.core.data.local.entities.CartItem
import com.example.core.data.local.entities.Label
import com.example.core.data.local.entities.Order
import com.example.core.data.local.entities.OrderProductCrossRef
import com.example.core.data.local.entities.Product
import com.example.core.utils.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RestoDao {
    @get:Rule
    val instantTastExecutorRule = InstantTaskExecutorRule()

    lateinit var noteDatabase: NoteDatabase
    lateinit var productDao: ProductDao
    lateinit var labelDao: LabelDao
    lateinit var cartItemDao: CartItemDao
    lateinit var orderDao: OrderDao

    private val product1 = Product(productId = 1, productName = "Product 1", productPrice = 5000, stock = 10)
    private val product2 = Product(productId = 2, productName = "Product 2", productPrice = 4500, stock = 15)
    private val label1 = Label(labelId = 1, labelName = "Label 1")
    private val label2 = Label(labelId = 2, labelName = "Label 2")
    private val order = Order(orderId = 1, orderName = "Arya Rezza", orderStatus = "CHECKOUT", paymentMethod = "CREDIT CARD")

    @Before
    fun setUp() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries().build()
        productDao = noteDatabase.productDao()
        labelDao = noteDatabase.labelDao()
        cartItemDao = noteDatabase.cartItemDao()
        orderDao = noteDatabase.orderDao()

        runBlocking {
            labelDao.insertLabel(label = label1)
            labelDao.insertLabel(label = label2)
            productDao.placeProductWithLabels(product = product1, labelIds = listOf(label1.labelId, label2.labelId))
            productDao.placeProductWithLabels(product = product2, labelIds = listOf(label1.labelId, label2.labelId))
        }
    }

    /**
     * === SECTION 1 ===
     * fungsi ketika pengguna ingin memasukan produk ke keranjang
     * */
    @Test
    fun testInsertCartItem() = runBlocking {
        /**
         * pengguna pilih produk
         * */
        val cartItem = CartItem(productId = 1, quantity = 2)

        /**
         * pengguna memasukan produk ke keranjang
         * */
        val insertedId = cartItemDao.insertCartItem(cartItem = cartItem)
        val actualCartItem = cartItemDao.getCartItemWithProductById(cartItemId = insertedId).getOrAwaitValue().cartItem

        /**
         * check apakah benar dan sudah sesuai kriteria
         * */
        Assert.assertEquals(cartItem.copy(cartItemId = insertedId), actualCartItem)
    }

    /**
     * === SECTION 2 ===
     * fungsi ini ketika pengguna ingin menghapus produk dari keranjang
     * */
    @Test
    fun testDeleteCartItem() = runBlocking {
        /**
         * pengguna pilih produk
         * */
        val cartItem = CartItem(productId = 2, quantity = 3)

        /**
         * pengguna memasukan produk ke keranjang
         * */
        val insertedId = cartItemDao.insertCartItem(cartItem = cartItem)
        val actualCartItem = cartItemDao.getCartItemWithProductById(cartItemId = insertedId).getOrAwaitValue().cartItem

        /**
         * check apakah benar dan sudah sesuai kriteria
         * */
        Assert.assertEquals(cartItem.copy(cartItemId = insertedId), actualCartItem)

        /**
         * pengguna mencoba melakukan hapus produk dari keranjang
         * */
        cartItemDao.deleteCartItem(cartItem = actualCartItem)
        val deletedCartItem = cartItemDao.getCartItemWithProductById(cartItemId = insertedId).getOrAwaitValue()

        /**
         * check apakah benar sudah terhapus
         * */
        Assert.assertNull(deletedCartItem)
    }

    /**
     * === SECTION 3 ===
     * fungsi ini dibuat ketika pengguna setelah memasukan produk ke keranjang
     * dan pengguna ingin langsung checkout dari keranjang
     * */
    @Test
    fun testCartToOrder() = runBlocking {
        /**
         * pengguna memilih beberapa produk
         * */
        val cartItem = listOf(
            CartItem(productId = 1, quantity = 2),
            CartItem(productId = 2, quantity = 3)
        )

        /**
         * beberapa produk dimasukan ke keranjang
         * dan melakukan order langsung dari keranjang
         * */
        cartItem.forEach {
            cartItemDao.insertCartItem(cartItem = it)
            productDao.reduceProductStock(productId = it.productId, quantity = it.quantity)
        }

        /**
         * check apakah sudah benar masuk ke keranjang
         * */
        Assert.assertEquals(2, cartItemDao.getCartItemsWithProduct().getOrAwaitValue().size)

        /**
         * check apakah stok produk berkurang
         * */
        val product = productDao.getProductWithLabelsById(productId = 1)
        val actualStock = product.getOrAwaitValue().product.stock

        Assert.assertEquals(8, actualStock)

        /**
         * proses ketika pengguna ingin langsung checkout ketika pengguna setelah memasukan produk
         * ke keranjang dan langsung checkout
         * */
        val cartItems = cartItemDao.getCartItemsWithProduct().getOrAwaitValue().map {
            it.product.productId
        }

        orderDao.placeOrderWithProducts(order = order, productIds = cartItems)
        val orderWithProduct = orderDao.getOrdersWithProducts().getOrAwaitValue()
        val actual = orderDao.getOrdersWithProductsById(orderId = 1)

        /**
         * check apakah sudah benar dan sudah sesuai kriteria
         * */
        Assert.assertEquals(1, orderWithProduct.size)
        Assert.assertEquals(product1.copy(stock = 8), actual.getOrAwaitValue().products[0])
        Assert.assertEquals(product2.copy(stock = 12), actual.getOrAwaitValue().products[1])
    }


    @After
    fun tearDown() {
        noteDatabase.close()
    }
}