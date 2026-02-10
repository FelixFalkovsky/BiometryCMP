package dev.enumset.biometry

/**
 * Parameters for a biometric authentication request.
 *
 * Extracted into a separate class (instead of individual parameters in [BiometryAuthenticator.authenticate])
 * so that adding new options does not break existing interface implementations (Open/Closed Principle).
 *
 * @property title title of the authentication dialog
 * @property subtitle subtitle (optional)
 * @property negativeButtonText cancel button text; not used on Android when [allowDeviceCredentials] is true
 * @property allowDeviceCredentials allow authentication via PIN / password / pattern
 */
data class AuthenticationRequest(
    val title: String,
    val subtitle: String? = null,
    val negativeButtonText: String? = null,
    val allowDeviceCredentials: Boolean = false
)
