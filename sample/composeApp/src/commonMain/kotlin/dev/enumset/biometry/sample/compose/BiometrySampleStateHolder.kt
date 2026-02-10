package dev.enumset.biometry.sample.compose

import dev.enumset.biometry.AuthenticationRequest
import dev.enumset.biometry.BiometryAvailability
import dev.enumset.biometry.BiometryAuthenticator
import dev.enumset.biometry.BiometryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BiometrySampleStateHolder(
    private val authenticator: BiometryAuthenticator,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(BiometrySampleUiState())
    val state: StateFlow<BiometrySampleUiState> = _state.asStateFlow()

    fun openSheet() {
        _state.update { it.copy(sheetVisible = true) }
    }

    fun closeSheet() {
        _state.update { it.copy(sheetVisible = false) }
    }

    fun loadAvailability() {
        scope.launch {
            val availability: BiometryAvailability = authenticator.isBiometryAvailable()
            withContext(Dispatchers.Main) {
                _state.update {
                    it.copy(
                        biometryTypeText = if (availability.isAvailable) {
                            availability.biometryType.toString()
                        } else {
                            availability.errorMessage ?: "Unavailable"
                        },
                        isAvailable = availability.isAvailable
                    )
                }
            }
        }
    }

    /** Closes sheet, launches authentication, updates success/failure. */
    fun runAuth(
        request: AuthenticationRequest = AuthenticationRequest(
            title = "Sign in",
            subtitle = "Confirm your identity",
            negativeButtonText = "Cancel",
            allowDeviceCredentials = true
        )
    ) {
        scope.launch {
            withContext(Dispatchers.Main) { closeSheet() }
            val result = authenticator.authenticate(request)
            withContext(Dispatchers.Main) {
                _state.update {
                    it.copy(
                        authSuccess = result is BiometryResult.Success,
                        authFailure = result !is BiometryResult.Success
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _state.update { it.copy(authSuccess = false) }
    }

    fun resetFailure() {
        _state.update { it.copy(authFailure = false) }
    }
}
