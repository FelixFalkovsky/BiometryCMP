package com.enumSet.biometry.sample.compose

import com.enumSet.biometry.BiometryAvailability
import com.enumSet.biometry.BiometryAuthenticator
import com.enumSet.biometry.BiometryResult
import com.enumSet.biometry.BiometryType

/**
 * Fake [BiometryAuthenticator] для тестов (подмена реализации, DIP).
 */
class FakeBiometryAuthenticator(
    private val availability: BiometryAvailability = BiometryAvailability(
        isAvailable = true,
        biometryType = BiometryType.FACE,
        errorMessage = null
    ),
    private val authResult: BiometryResult = BiometryResult.Success
) : BiometryAuthenticator {

    var availabilityCallCount = 0
        private set
    var authenticateCallCount = 0
        private set

    override suspend fun isBiometryAvailable(): BiometryAvailability {
        availabilityCallCount++
        return availability
    }

    override suspend fun authenticate(
        title: String,
        subtitle: String?,
        negativeButtonText: String?,
        allowDeviceCredentials: Boolean
    ): BiometryResult {
        authenticateCallCount++
        return authResult
    }
}
