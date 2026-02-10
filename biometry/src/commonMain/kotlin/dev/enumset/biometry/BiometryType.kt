package dev.enumset.biometry

/**
 * Type of biometry available on the device.
 */
enum class BiometryType {
    /** Biometry is unavailable or not enrolled. */
    NONE,

    /** Fingerprint authentication (Touch ID on iOS, Fingerprint on Android). */
    FINGERPRINT,

    /** Face authentication (Face ID on iOS, Face on Android). */
    FACE,

    /** Iris authentication (some Android devices). */
    IRIS
}
