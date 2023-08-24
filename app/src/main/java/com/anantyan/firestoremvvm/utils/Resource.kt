package com.anantyan.firestoremvvm.utils

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val exception: String? = null, val data: T? = null) : Resource<T>()
}
