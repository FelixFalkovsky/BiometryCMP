package dev.enumset.biometry

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.util.concurrent.Executor

private const val AUTHENTICATORS_BIOMETRIC_STRONG = 0x0000000F
private const val AUTHENTICATORS_DEVICE_CREDENTIAL = 0x00008000


fun setFragmentActivityForBiometry(activity: FragmentActivity?) {
    AndroidBiometryHolder.fragmentActivity = activity
}

internal object AndroidBiometryHolder {
    var fragmentActivity: FragmentActivity? = null
}

@Suppress("CONFLICTING_OVERLOADS")
actual fun createBiometryAuthenticator(): BiometryAuthenticator {
    val activity = AndroidBiometryHolder.fragmentActivity
    return if (activity != null) AndroidBiometryAuthenticator(activity)
    else NoOpBiometryAuthenticator()
}

private class NoOpBiometryAuthenticator : BiometryAuthenticator {
    override suspend fun isBiometryAvailable(): BiometryAvailability =
        BiometryAvailability(
            isAvailable = false,
            biometryType = BiometryType.NONE,
            errorMessage = "FragmentActivity not set. Call setFragmentActivityForBiometry(activity) first."
        )

    override suspend fun authenticate(request: AuthenticationRequest): BiometryResult =
        BiometryResult.Error("FragmentActivity not set", -1)
}

internal class AndroidBiometryAuthenticator(
    private val activity: FragmentActivity
) : BiometryAuthenticator {

    private val context get() = activity.applicationContext
    private val mainExecutor: Executor = Executor { r -> Handler(Looper.getMainLooper()).post(r) }

    override suspend fun isBiometryAvailable(): BiometryAvailability = suspendCoroutine { cont ->
        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AUTHENTICATORS_BIOMETRIC_STRONG or AUTHENTICATORS_DEVICE_CREDENTIAL
        } else {
            @Suppress("DEPRECATION")
            AUTHENTICATORS_BIOMETRIC_STRONG
        }
        val result = BiometricManager.from(context).canAuthenticate(authenticators)
        val (isAvailable, type, errorMessage) = when (result) {
            BIOMETRIC_SUCCESS -> Triple(true, biometryTypeFromDevice(), null)
            BIOMETRIC_ERROR_NONE_ENROLLED -> Triple(false, BiometryType.NONE, "No biometric or device credential enrolled")
            BIOMETRIC_ERROR_NO_HARDWARE -> Triple(false, BiometryType.NONE, "No biometric hardware")
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> Triple(false, BiometryType.NONE, "Biometric hardware unavailable")
            BIOMETRIC_ERROR_UNSUPPORTED -> Triple(false, BiometryType.NONE, "Biometrics not supported")
            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> Triple(false, BiometryType.NONE, "Security update required")
            else -> Triple(false, BiometryType.NONE, "Unknown error (code=$result)")
        }
        cont.resume(BiometryAvailability(isAvailable, type, errorMessage))
    }

    override suspend fun authenticate(
        request: AuthenticationRequest
    ): BiometryResult = suspendCancellableCoroutine { cont ->
        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && request.allowDeviceCredentials) {
            AUTHENTICATORS_BIOMETRIC_STRONG or AUTHENTICATORS_DEVICE_CREDENTIAL
        } else {
            AUTHENTICATORS_BIOMETRIC_STRONG
        }
        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(request.title)
            .apply { request.subtitle?.let { setSubtitle(it) } }
            // BiometricPrompt forbids negative button when device credentials are allowed.
            // Set the button only when biometry-only mode is used.
            .apply {
                if (!request.allowDeviceCredentials) {
                    setNegativeButtonText(request.negativeButtonText ?: "Cancel")
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoBuilder.setAllowedAuthenticators(authenticators)
        } else {
            @Suppress("DEPRECATION")
            promptInfoBuilder.setDeviceCredentialAllowed(request.allowDeviceCredentials)
        }
        val promptInfo = promptInfoBuilder.build()
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                cont.resume(BiometryResult.Success)
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                cont.resume(
                    BiometryResultMapper.map(
                        success = false,
                        errorCode = errorCode,
                        errorMessage = errString.toString(),
                        isCancelledCode = { code ->
                            code == BiometricPrompt.ERROR_USER_CANCELED ||
                                code == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        }
                    )
                )
            }
            override fun onAuthenticationFailed() {
                // Retry allowed; don't complete yet
            }
        }
        val biometricPrompt = BiometricPrompt(activity, mainExecutor, callback)
        Handler(Looper.getMainLooper()).post {
            if (cont.isCancelled) return@post
            biometricPrompt.authenticate(promptInfo)
        }
        cont.invokeOnCancellation { biometricPrompt.cancelAuthentication() }
    }

    private fun biometryTypeFromDevice(): BiometryType {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(AUTHENTICATORS_BIOMETRIC_STRONG)) {
            BIOMETRIC_SUCCESS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val pm = context.packageManager
                    if (pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_FACE)) return BiometryType.FACE
                    if (pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_FINGERPRINT)) return BiometryType.FINGERPRINT
                }
                BiometryType.FINGERPRINT
            }
            else -> BiometryType.NONE
        }
    }
}
