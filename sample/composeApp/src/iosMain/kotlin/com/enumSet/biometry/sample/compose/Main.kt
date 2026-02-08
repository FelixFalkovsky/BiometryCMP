package com.enumSet.biometry.sample.compose

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Точка входа для iOS: только жизненный цикл.
 * Swift передаёт создание/обновление/освобождение через UIViewControllerRepresentable;
 * вёрстка и логика — в [BiometrySampleScreen] (KMP).
 */
fun BiometrySampleViewController(): UIViewController =
    ComposeUIViewController {
        BiometrySampleScreen()
    }
