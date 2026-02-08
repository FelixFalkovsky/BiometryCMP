package com.enumSet.biometry.sample.compose

import com.enumSet.biometry.BiometryAvailability
import com.enumSet.biometry.BiometryAuthenticator
import com.enumSet.biometry.BiometryResult
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
                            availability.errorMessage ?: "Недоступно"
                        },
                        isAvailable = availability.isAvailable
                    )
                }
            }
        }
    }

    /** Закрывает sheet, запускает авторизацию, обновляет success/failure. */
    fun runAuth(
        title: String = "Вход в приложение",
        subtitle: String? = "Подтвердите личность",
        negativeButtonText: String? = "Отмена",
        allowDeviceCredentials: Boolean = true
    ) {
        scope.launch {
            withContext(Dispatchers.Main) { closeSheet() }
            val result = authenticator.authenticate(
                title = title,
                subtitle = subtitle,
                negativeButtonText = negativeButtonText,
                allowDeviceCredentials = allowDeviceCredentials
            )
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
