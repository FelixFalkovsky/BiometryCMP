package dev.enumset.biometry.sample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.enumset.biometry.AuthenticationRequest
import dev.enumset.biometry.BiometryResult
import dev.enumset.biometry.createBiometryAuthenticator
import dev.enumset.biometry.setFragmentActivityForBiometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Demo screen for testing the biometry-auth library.
 * Shows biometry availability check and authentication dialog.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnCheck: Button
    private lateinit var btnAuthenticate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFragmentActivityForBiometry(this)

        statusText = findViewById(R.id.statusText)
        btnCheck = findViewById(R.id.btnCheck)
        btnAuthenticate = findViewById(R.id.btnAuthenticate)

        btnCheck.setOnClickListener { checkAvailability() }
        btnAuthenticate.setOnClickListener { runAuthentication() }
    }

    override fun onDestroy() {
        setFragmentActivityForBiometry(null)
        super.onDestroy()
    }

    private fun checkAvailability() {
        lifecycleScope.launch(Dispatchers.Main) {
            statusText.text = "Checking…"
            val availability = withContext(Dispatchers.Default) {
                createBiometryAuthenticator().isBiometryAvailable()
            }
            statusText.text = if (availability.isAvailable) {
                "Available: ${availability.biometryType}"
            } else {
                "Unavailable: ${availability.errorMessage ?: "—"}"
            }
        }
    }

    private fun runAuthentication() {
        lifecycleScope.launch(Dispatchers.Main) {
            statusText.text = "Waiting for authentication…"
            val result = createBiometryAuthenticator().authenticate(
                AuthenticationRequest(
                    title = "Sign in",
                    subtitle = "Confirm your identity to continue",
                    negativeButtonText = "Cancel",
                    allowDeviceCredentials = true
                )
            )
            statusText.text = when (result) {
                is BiometryResult.Success -> "Success"
                is BiometryResult.Cancelled -> "Cancelled"
                is BiometryResult.Error -> "Error: ${result.message} (code ${result.code})"
            }
            Toast.makeText(this@MainActivity, statusText.text, Toast.LENGTH_SHORT).show()
        }
    }
}
