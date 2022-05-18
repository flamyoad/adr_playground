package com.flamyoad.basicpaginationconcatadapter.common


sealed class State<out T : Any> {
    data class Success<out T : Any>(val value: T) : State<T>()
    data class Error(val exception: Exception? = null, val message: String = ""): State<Nothing>()
}