package dev.enumset.biometry.sample.compose

/**
 * Immutable UI state of the biometry sample screen (convenient for tests and debugging).
 */
data class BiometrySampleUiState(
    val authSuccess: Boolean = false,
    val authFailure: Boolean = false,
    val sheetVisible: Boolean = false,
    val biometryTypeText: String = "…",
    val isAvailable: Boolean = false
)
