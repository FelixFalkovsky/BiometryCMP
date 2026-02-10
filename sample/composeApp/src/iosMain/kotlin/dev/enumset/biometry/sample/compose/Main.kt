package dev.enumset.biometry.sample.compose

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * iOS entry point: lifecycle only.
 * Swift delegates creation/update/disposal via UIViewControllerRepresentable;
 * layout and logic are in [BiometrySampleScreen] (KMP).
 */
fun BiometrySampleViewController(): UIViewController =
    ComposeUIViewController {
        BiometrySampleScreen()
    }
