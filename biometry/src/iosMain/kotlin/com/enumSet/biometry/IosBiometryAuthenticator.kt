package com.enumSet.biometry

actual fun createBiometryAuthenticator(): BiometryAuthenticator =
    IosBiometryAuthenticator()

internal class IosBiometryAuthenticator : BiometryAuthenticator {
    override suspend fun isBiometryAvailable(): BiometryAvailability {
        // TODO: Step 4 — LAContext canEvaluatePolicy
        return BiometryAvailability(
            isAvailable = false,
            biometryType = BiometryType.NONE,
            errorMessage = "Not implemented"
        )
    }

    override suspend fun authenticate(
        title: String,
        subtitle: String?,
        negativeButtonText: String?,
        allowDeviceCredentials: Boolean
    ): BiometryResult {
        // TODO: Step 4 — LAContext evaluatePolicy
        return BiometryResult.Cancelled
    }
}
