package com.enumSet.biometry

interface BiometryAuthenticator {
    suspend fun isBiometryAvailable(): BiometryAvailability
    suspend fun authenticate(
        title: String,
        subtitle: String? = null,
        negativeButtonText: String? = null,
        allowDeviceCredentials: Boolean = false
    ): BiometryResult
}

expect fun createBiometryAuthenticator(): BiometryAuthenticator
