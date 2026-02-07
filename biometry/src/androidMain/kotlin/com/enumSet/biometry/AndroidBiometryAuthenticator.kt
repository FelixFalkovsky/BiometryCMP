package com.enumSet.biometry

actual fun createBiometryAuthenticator(): BiometryAuthenticator =
    AndroidBiometryAuthenticator()

internal class AndroidBiometryAuthenticator : BiometryAuthenticator {
    override suspend fun isBiometryAvailable(): BiometryAvailability {
        // TODO: Step 3 — BiometricManager.from(context).canAuthenticate(...)
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
        // TODO: Step 3 — BiometricPrompt + suspendCancellableCoroutine
        return BiometryResult.Cancelled
    }
}
