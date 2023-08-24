package com.anantyan.firestoremvvm.model

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val imgUrl: String = "",
    val tags: List<String> = listOf(),
    val publish: Boolean = false,
    val createAt: Timestamp? = null,
    val updateAt: Timestamp? = null
)
