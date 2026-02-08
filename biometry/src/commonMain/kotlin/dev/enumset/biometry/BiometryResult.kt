package dev.enumset.biometry

/**
 * Результат попытки биометрической авторизации.
 * Вся семантика (Success / Cancelled / Error) определяется в commonMain;
 * платформы передают только сырые данные и предикат "код отмены".
 */
sealed class BiometryResult {
    data object Success : BiometryResult()
    data object Cancelled : BiometryResult()
    data class Error(val message: String?, val code: Int) : BiometryResult()

    companion object {
        /**
         * Строит [BiometryResult] из ответа платформенного API (Dependency Inversion:
         * общая логика не зависит от конкретных кодов iOS/Android).
         *
         * @param success успех авторизации
         * @param errorCode код ошибки (null при success или если платформа не дала код)
         * @param errorMessage сообщение от платформы
         * @param isCancelledCode предикат: true, если код означает отмену пользователем/системой
         */
        fun fromAuthResponse(
            success: Boolean,
            errorCode: Int?,
            errorMessage: String?,
            isCancelledCode: (Int) -> Boolean
        ): BiometryResult =
            when {
                success -> Success
                errorCode != null && isCancelledCode(errorCode) -> Cancelled
                else -> Error(errorMessage, errorCode ?: -1)
            }
    }
}
