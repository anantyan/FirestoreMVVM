package com.example.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.data.local.dao.CartItemDao
import com.example.core.data.local.dao.LabelDao
import com.example.core.data.local.dao.NoteDao
import com.example.core.data.local.dao.OrderDao
import com.example.core.data.local.dao.ProductDao
import com.example.core.data.local.entities.CartItem
import com.example.core.data.local.entities.Label
import com.example.core.data.local.entities.LabelProductCrossRef
import com.example.core.data.local.entities.Note
import com.example.core.data.local.entities.Order
import com.example.core.data.local.entities.OrderProductCrossRef
import com.example.core.data.local.entities.Product

@Database(
    entities = [
        Note::class,
        Product::class,
        Label::class,
        LabelProductCrossRef::class,
        CartItem::class,
        Order::class,
        OrderProductCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun productDao(): ProductDao
    abstract fun labelDao(): LabelDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao
}