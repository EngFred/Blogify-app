package com.engineerfred.kotlin.next.utils

sealed class Response<out X> {
    data object Undefined : Response<Nothing>()
    data class Failure( val exception: String ) : Response<Nothing>()
    data class Success<X>(val data: X) : Response<X>()
}