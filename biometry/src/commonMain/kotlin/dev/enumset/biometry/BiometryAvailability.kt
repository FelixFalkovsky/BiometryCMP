package dev.enumset.biometry

/**
 * Result of a biometry availability check on the device.
 *
 * @property isAvailable true if biometry is enrolled and ready to use
 * @property biometryType type of available biometry ([BiometryType.NONE] if unavailable)
 * @property errorMessage description of the unavailability reason (null if available)
 */
data class BiometryAvailability(
    val isAvailable: Boolean,
    val biometryType: BiometryType,
    val errorMessage: String? = null
)
