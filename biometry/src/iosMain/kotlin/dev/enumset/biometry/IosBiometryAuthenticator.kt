package dev.enumset.biometry

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.cinterop.ExperimentalForeignApi
import platform.LocalAuthentication.LAContext
import kotlin.coroutines.resume


private object LAPolicy {
    const val DeviceOwnerAuthenticationWithBiometrics = 1L
    const val DeviceOwnerAuthentication = 2L
}

private object LAErrorCode {
    const val AuthenticationFailed = -1
    const val UserCancel = -2
    const val UserFallback = -3
    const val SystemCancel = -4
}

private fun laBiometryTypeToBiometryType(laType: Long): BiometryType = when (laType) {
    1L -> BiometryType.FACE       // LABiometryTypeFaceID
    2L -> BiometryType.FINGERPRINT // LABiometryTypeTouchID
    else -> BiometryType.FINGERPRINT
}

@OptIn(ExperimentalForeignApi::class)
private fun LAContext.evaluateBiometryAvailability(): BiometryAvailability {
    val canEvaluate = runCatching {
        canEvaluatePolicy(LAPolicy.DeviceOwnerAuthenticationWithBiometrics, null)
    }.getOrElse { false }
    return if (canEvaluate) {
        val type = runCatching { laBiometryTypeToBiometryType(biometryType.toLong()) }
            .getOrElse { BiometryType.FINGERPRINT }
        BiometryAvailability(isAvailable = true, biometryType = type, errorMessage = null)
    } else {
        BiometryAvailability(
            isAvailable = false,
            biometryType = BiometryType.NONE,
            errorMessage = "Biometry not available or not enrolled"
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
@Suppress("CONFLICTING_OVERLOADS")
actual fun createBiometryAuthenticator(): BiometryAuthenticator =
    IosBiometryAuthenticator()

@OptIn(ExperimentalForeignApi::class)
internal class IosBiometryAuthenticator : BiometryAuthenticator {

    override suspend fun isBiometryAvailable(): BiometryAvailability =
        suspendCancellableCoroutine { cont ->
            val context = LAContext()
            cont.resume(context.evaluateBiometryAvailability())
        }

    override suspend fun authenticate(
        title: String,
        subtitle: String?,
        negativeButtonText: String?,
        allowDeviceCredentials: Boolean
    ): BiometryResult = suspendCancellableCoroutine { cont ->
        val context = LAContext()
        val policy = if (allowDeviceCredentials) {
            LAPolicy.DeviceOwnerAuthentication
        } else {
            LAPolicy.DeviceOwnerAuthenticationWithBiometrics
        }
        context.evaluatePolicy(policy, title) { success, error ->
            val result = runCatching {
                BiometryResult.fromAuthResponse(
                    success = success,
                    errorCode = error?.code?.toInt(),
                    errorMessage = error?.localizedDescription,
                    isCancelledCode = { code ->
                        code == LAErrorCode.UserCancel ||
                            code == LAErrorCode.SystemCancel ||
                            code == LAErrorCode.UserFallback
                    }
                )
            }.getOrElse {
                BiometryResult.Error(message = error?.localizedDescription ?: "Unknown error", code = -1)
            }
            cont.resume(result)
        }
    }
}
