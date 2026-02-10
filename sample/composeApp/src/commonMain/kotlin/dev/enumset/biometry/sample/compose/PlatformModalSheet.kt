package dev.enumset.biometry.sample.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

/**
 * Modal bottom sheet for Android and iOS based on [ModalBottomSheet] (Material3).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformModalSheet(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        content()
    }
}
