package dev.enumset.biometry.sample.compose

import dev.enumset.biometry.BiometryAvailability
import dev.enumset.biometry.BiometryAuthenticator
import dev.enumset.biometry.BiometryResult
import dev.enumset.biometry.BiometryType

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
