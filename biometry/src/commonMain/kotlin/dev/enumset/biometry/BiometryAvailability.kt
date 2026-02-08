package dev.enumset.biometry

data class BiometryAvailability(
    val isAvailable: Boolean,
    val biometryType: BiometryType,
    val errorMessage: String? = null
)
