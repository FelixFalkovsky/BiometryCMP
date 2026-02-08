package com.enumSet.biometry.sample.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enumSet.biometry.BiometryAuthenticator
import com.enumSet.biometry.createBiometryAuthenticator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometrySampleScreen(
    authenticator: BiometryAuthenticator = createBiometryAuthenticator()
) {
    val scope = rememberCoroutineScope()
    val holder = remember(authenticator) {
        BiometrySampleStateHolder(authenticator, scope)
    }
    val uiState = holder.state.collectAsState().value

    LaunchedEffect(uiState.sheetVisible) {
        if (uiState.sheetVisible) {
            holder.loadAvailability()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                uiState.authSuccess -> SuccessContent(onRepeat = { holder.resetSuccess() })
                uiState.authFailure -> FailureContent(onRepeat = { holder.resetFailure() })
                else -> MainContent(onBiometryClick = { holder.openSheet() })
            }
        }
    }

    if (uiState.sheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { holder.closeSheet() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            SheetContent(
                biometryTypeText = uiState.biometryTypeText,
                isAvailable = uiState.isAvailable,
                onAuthClick = { holder.runAuth() }
            )
        }
    }
}

@Composable
private fun SuccessContent(onRepeat: () -> Unit) {
    Text(
        "✓",
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Text("Success", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(16.dp))
    OutlinedButton(onClick = onRepeat) {
        Text("Повторить")
    }
}

@Composable
private fun FailureContent(onRepeat: () -> Unit) {
    Text(
        "✕",
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.error
    )
    Text("Failure", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(16.dp))
    OutlinedButton(onClick = onRepeat) {
        Text("Повторить")
    }
}

@Composable
private fun MainContent(onBiometryClick: () -> Unit) {
    Text(
        "Biometry",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = onBiometryClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Биометрия")
    }
}

@Composable
private fun SheetContent(
    biometryTypeText: String,
    isAvailable: Boolean,
    onAuthClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            biometryTypeText,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAuthClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isAvailable
        ) {
            Text("Войти по биометрии")
        }
    }
}
