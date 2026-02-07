package com.enumSet.biometry

sealed class BiometryResult {
    data object Success : BiometryResult()
    data object Cancelled : BiometryResult()
    data class Error(val message: String?, val code: Int) : BiometryResult()
}
