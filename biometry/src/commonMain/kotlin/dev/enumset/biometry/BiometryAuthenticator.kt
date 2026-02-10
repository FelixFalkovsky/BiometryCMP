package dev.enumset.biometry

/**
 * Biometric authentication contract (Interface Segregation: only what the client needs).
 *
 * Platform implementations (Android / iOS) are provided via [createBiometryAuthenticator].
 * For unit tests it is sufficient to implement this interface with a fake (Dependency Inversion).
 */
interface BiometryAuthenticator {

    /**
     * Checks whether biometry is available on the device.
     *
     * @return [BiometryAvailability] with the biometry type and error description (if unavailable)
     */
    suspend fun isBiometryAvailable(): BiometryAvailability

    /**
     * Launches authentication with the given parameters.
     *
     * @param request authentication dialog parameters (see [AuthenticationRequest])
     * @return [BiometryResult] — success, cancellation, or error
     */
    suspend fun authenticate(request: AuthenticationRequest): BiometryResult
}

/**
 * Factory for obtaining a platform-specific [BiometryAuthenticator] implementation.
 *
 * - Android: before calling, set the [FragmentActivity] via `setFragmentActivityForBiometry(activity)`.
 * - iOS: works without additional setup.
 */
expect fun createBiometryAuthenticator(): BiometryAuthenticator
