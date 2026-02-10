package dev.enumset.biometry.sample.compose

import dev.enumset.biometry.AuthenticationRequest
import dev.enumset.biometry.BiometryAvailability
import dev.enumset.biometry.BiometryAuthenticator
import dev.enumset.biometry.BiometryResult
import dev.enumset.biometry.BiometryType

/**
 * Fake [BiometryAuthenticator] for unit tests (DIP: swappable implementation).
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
    var lastRequest: AuthenticationRequest? = null
        private set

    override suspend fun isBiometryAvailable(): BiometryAvailability {
        availabilityCallCount++
        return availability
    }

    override suspend fun authenticate(request: AuthenticationRequest): BiometryResult {
        authenticateCallCount++
        lastRequest = request
        return authResult
    }
}
