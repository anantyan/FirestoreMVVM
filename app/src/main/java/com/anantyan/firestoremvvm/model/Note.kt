package com.anantyan.firestoremvvm.model

data class Note(
    val id: String? = "",
    val title: String? = "",
    val content: String? = "",
    val imgUrl: String? = "",
    val tags: List<String>? = listOf(),
    val publish: Boolean? = false
)
