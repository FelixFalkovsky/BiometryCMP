package com.enumSet.biometry.sample.compose

import com.enumSet.biometry.BiometryResult
import com.enumSet.biometry.BiometryType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BiometrySampleStateHolderTest {

    @Test
    fun openSheet_setsSheetVisible() = runTest {
        val fake = FakeBiometryAuthenticator()
        val holder = BiometrySampleStateHolder(fake, this)

        holder.openSheet()
        runCurrent()

        assertTrue(holder.state.value.sheetVisible)
    }

    @Test
    fun loadAvailability_updatesStateFromAuthenticator() = runTest {
        val fake = FakeBiometryAuthenticator(
            availability = com.enumSet.biometry.BiometryAvailability(
                isAvailable = true,
                biometryType = BiometryType.FINGERPRINT,
                errorMessage = null
            )
        )
        val holder = BiometrySampleStateHolder(fake, this)

        holder.loadAvailability()
        advanceUntilIdle()

        assertEquals("FINGERPRINT", holder.state.value.biometryTypeText)
        assertTrue(holder.state.value.isAvailable)
        assertEquals(1, fake.availabilityCallCount)
    }

    @Test
    fun loadAvailability_whenUnavailable_setsErrorMessage() = runTest {
        val fake = FakeBiometryAuthenticator(
            availability = com.enumSet.biometry.BiometryAvailability(
                isAvailable = false,
                biometryType = com.enumSet.biometry.BiometryType.NONE,
                errorMessage = "Not enrolled"
            )
        )
        val holder = BiometrySampleStateHolder(fake, this)

        holder.loadAvailability()
        advanceUntilIdle()

        assertEquals("Not enrolled", holder.state.value.biometryTypeText)
        assertFalse(holder.state.value.isAvailable)
    }

    @Test
    fun runAuth_onSuccess_setsAuthSuccess() = runTest {
        val fake = FakeBiometryAuthenticator(authResult = BiometryResult.Success)
        val holder = BiometrySampleStateHolder(fake, this)

        holder.runAuth()
        advanceUntilIdle()

        assertTrue(holder.state.value.authSuccess)
        assertFalse(holder.state.value.authFailure)
        assertFalse(holder.state.value.sheetVisible)
        assertEquals(1, fake.authenticateCallCount)
    }

    @Test
    fun runAuth_onFailure_setsAuthFailure() = runTest {
        val fake = FakeBiometryAuthenticator(
            authResult = BiometryResult.Error("Failed", -1)
        )
        val holder = BiometrySampleStateHolder(fake, this)

        holder.runAuth()
        advanceUntilIdle()

        assertFalse(holder.state.value.authSuccess)
        assertTrue(holder.state.value.authFailure)
    }

    @Test
    fun resetSuccess_clearsAuthSuccess() = runTest {
        val fake = FakeBiometryAuthenticator(authResult = BiometryResult.Success)
        val holder = BiometrySampleStateHolder(fake, this)
        holder.runAuth()
        advanceUntilIdle()
        assertTrue(holder.state.value.authSuccess)

        holder.resetSuccess()
        runCurrent()

        assertFalse(holder.state.value.authSuccess)
    }

    @Test
    fun resetFailure_clearsAuthFailure() = runTest {
        val fake = FakeBiometryAuthenticator(authResult = BiometryResult.Cancelled)
        val holder = BiometrySampleStateHolder(fake, this)
        holder.runAuth()
        advanceUntilIdle()
        assertTrue(holder.state.value.authFailure)

        holder.resetFailure()
        runCurrent()

        assertFalse(holder.state.value.authFailure)
    }
}
