package dev.enumset.biometry

/**
 * Result of a biometric authentication attempt.
 *
 * All outcome variants are defined in commonMain:
 * - [Success] — authentication passed.
 * - [Cancelled] — authentication was cancelled by the user or system.
 * - [Error] — an error occurred (no hardware, timeout, lockout, etc.).
 */
sealed class BiometryResult {

    /** Authentication succeeded. */
    data object Success : BiometryResult()

    /** Authentication was cancelled by the user or system. */
    data object Cancelled : BiometryResult()

    /**
     * Authentication error.
     *
     * @property message human-readable error description from the platform
     * @property code platform error code (-1 if unknown)
     */
    data class Error(val message: String?, val code: Int) : BiometryResult()
}
