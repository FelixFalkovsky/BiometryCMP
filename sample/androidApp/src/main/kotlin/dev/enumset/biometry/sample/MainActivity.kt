package dev.enumset.biometry.sample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.enumset.biometry.BiometryResult
import dev.enumset.biometry.createBiometryAuthenticator
import dev.enumset.biometry.setFragmentActivityForBiometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Демо-экран для проверки работы библиотеки biometry-auth.
 * Показывает проверку доступности биометрии и запуск диалога аутентификации.
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
            statusText.text = "Проверка…"
            val availability = withContext(Dispatchers.Default) {
                createBiometryAuthenticator().isBiometryAvailable()
            }
            statusText.text = if (availability.isAvailable) {
                "Доступно: ${availability.biometryType}"
            } else {
                "Недоступно: ${availability.errorMessage ?: "—"}"
            }
        }
    }

    private fun runAuthentication() {
        lifecycleScope.launch(Dispatchers.Main) {
            statusText.text = "Ожидание аутентификации…"
            val result = createBiometryAuthenticator().authenticate(
                title = "Вход в приложение",
                subtitle = "Подтвердите личность для продолжения",
                negativeButtonText = "Отмена",
                allowDeviceCredentials = true
            )
            statusText.text = when (result) {
                is BiometryResult.Success -> "Успешно"
                is BiometryResult.Cancelled -> "Отменено"
                is BiometryResult.Error -> "Ошибка: ${result.message} (код ${result.code})"
            }
            Toast.makeText(this@MainActivity, statusText.text, Toast.LENGTH_SHORT).show()
        }
    }
}
