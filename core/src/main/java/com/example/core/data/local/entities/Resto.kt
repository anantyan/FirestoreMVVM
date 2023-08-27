package com.example.core.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


/**
 * === SECTION 1 ===
 * entity untuk table PRODUK
 * */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val productName: String,
    val productPrice: Long,
    val stock: Int
)

/**
 * === SECTION 2 ===
 * entity untuk table LABEL
 * */
@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true)
    val labelId: Long = 0,
    val labelName: String
)

/**
 * entity untuk table relasi PRODUK & LABEL
 * */
@Entity(
    primaryKeys = ["productId", "labelId"],
    foreignKeys = [
        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"]),
        ForeignKey(entity = Label::class, parentColumns = ["labelId"], childColumns = ["labelId"])
    ]
)
data class LabelProductCrossRef(
    val productId: Long,
    val labelId: Long
)

/**
 * data class untuk menampilkan PRODUK dengan
 * berbagai macam LABEL
 * */
data class ProductWithLabels(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "productId",
        entityColumn = "labelId",
        associateBy = Junction(LabelProductCrossRef::class)
    )
    val labels: List<Label>
)

/**
 * data clas untuk menampilkan LABEL dengan
 * berbagai macam PRODUK
 * */
data class LabelWithProducts(
    @Embedded val label: Label,
    @Relation(
        parentColumn = "labelId",
        entityColumn = "productId",
        associateBy = Junction(LabelProductCrossRef::class)
    )
    val products: List<Product>
)

/**
 * === SECTION 3 ===
 * entity untuk tabel CART ITEM
 * */
@Entity(tableName = "cart_items",
    foreignKeys = [
        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val cartItemId: Long = 0,
    val productId: Long,
    val quantity: Int
)

/**
 * data class untuk menampilkan CART ITEM dengan
 * detail PRODUK
 * */
data class CartItemWithProduct(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val product: Product
)

/**
 * === SECTION 4 ===
 * entity dari table ORDERS
 * */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val orderName: String,
    val paymentMethod: String,
    val orderStatus: String
)

/**
 * entity dari relasi table ORDER & PRODUK
 * */
@Entity(tableName = "order_product",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(entity = Order::class, parentColumns = ["orderId"], childColumns = ["orderId"]),
        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])
    ]
)
data class OrderProductCrossRef(
    val orderId: Long,
    val productId: Long
)

/**
 * data class untuk menampilkan table ORDER dengan
 * berbagai macam PRODUK
 * */
data class OrderWithProducts(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "productId",
        associateBy = Junction(OrderProductCrossRef::class)
    )
    val products: List<Product>
)