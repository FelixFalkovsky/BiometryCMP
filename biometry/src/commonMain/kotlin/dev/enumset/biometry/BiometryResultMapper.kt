package dev.enumset.biometry

/**
 * Mapper from platform responses to [BiometryResult].
 *
 * Extracted from [BiometryResult.Companion] so the sealed class is responsible only for the
 * result model (SRP), while mapping logic lives separately.
 * Marked `internal` because only platform implementations need it.
 */
internal object BiometryResultMapper {

    /**
     * Builds a [BiometryResult] from a platform API response.
     *
     * Common logic does not depend on specific iOS/Android codes (Dependency Inversion):
     * the platform provides a [isCancelledCode] predicate defining which codes mean cancellation.
     *
     * @param success whether authentication succeeded
     * @param errorCode error code (null on success or if the platform did not provide one)
     * @param errorMessage message from the platform
     * @param isCancelledCode predicate: true if [errorCode] means user/system cancellation
     */
    fun map(
        success: Boolean,
        errorCode: Int?,
        errorMessage: String?,
        isCancelledCode: (Int) -> Boolean
    ): BiometryResult = when {
        success -> BiometryResult.Success
        errorCode != null && isCancelledCode(errorCode) -> BiometryResult.Cancelled
        else -> BiometryResult.Error(
            message = errorMessage,
            code = errorCode ?: UNKNOWN_ERROR_CODE
        )
    }

    private const val UNKNOWN_ERROR_CODE = -1
}
