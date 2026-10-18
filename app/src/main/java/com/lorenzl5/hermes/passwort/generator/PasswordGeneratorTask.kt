package com.lorenzl5.hermes.passwort.generator

import android.content.ClipboardManager
import android.content.Context
import com.lorenzl5.hermes.passwort.database.MetaData
import com.lorenzl5.hermes.passwort.helpers.PreferenceKeys
import java.util.concurrent.Executors

/**
 * Background password generation task.
 * Replaces AsyncTask (deprecated).
 */
class PasswordGeneratorTask(
    private val callback: (password: String) -> Unit
) {
    private val executor: java.util.concurrent.ExecutorService = Executors.newSingleThreadExecutor()

    fun execute(
        domain: String,
        username: String,
        masterPassword: String,
        deviceId: String,
        iteration: Int,
        hashIterations: Int,
        hashAlgorithm: String,
        bcryptCost: String,
        special: Int,
        lower: Int,
        upper: Int,
        numbers: Int,
        length: Int
    ) {
        executor.execute {
            val generator = PasswordGenerator(
                domain, username, masterPassword, deviceId,
                iteration, hashIterations, hashAlgorithm, bcryptCost
            )
            val password = generator.getPassword(special, lower, upper, numbers, length)
            try {
                generator.deleteFinalize()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            callback(password)
        }
    }

    fun cancel() {
        executor.shutdownNow()
    }
}
