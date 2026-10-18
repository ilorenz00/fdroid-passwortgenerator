package com.lorenzl5.hermes.passwort.helpers

import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import android.content.Context

object PreferenceKeys {
    const val BCRYPT_COST = "bcrypt_cost"
    const val BIND_TO_DEVICE_ENABLED = "bind_to_device_enabled"
    const val CLIPBOARD_ENABLED = "clipboard_enabled"
    const val HASH_ALGORITHM = "hash_algorithm"
    const val HASH_ITERATIONS = "hash_iterations"
    const val SALT_VALUE = "salt_value"
    const val TUTORIAL_DONE = "tutorial_done"
    const val MASTERPW_TUTORIAL_DONE = "masterpw_tutorial_done"

    fun getBcryptCost(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(BCRYPT_COST, "10") ?: "10"

    fun isBindToDevice(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(BIND_TO_DEVICE_ENABLED, false)

    fun isClipboardEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(CLIPBOARD_ENABLED, true)

    fun isTutorialDone(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(TUTORIAL_DONE, false)

    fun setTutorialDone(context: Context, done: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(TUTORIAL_DONE, done).apply()
    }

    fun isMasterpwTutorialDone(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(MASTERPW_TUTORIAL_DONE, false)

    fun setMasterpwTutorialDone(context: Context, done: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(MASTERPW_TUTORIAL_DONE, done).apply()
    }

    /** Returns device ID for salt binding */
    fun getDeviceId(context: Context): String {
        return if (isBindToDevice(context)) {
            // Use a stable identifier based on installation
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            var id = prefs.getString(SALT_VALUE, null)
            if (id == null) {
                id = java.util.UUID.randomUUID().toString()
                prefs.edit().putString(SALT_VALUE, id).apply()
            }
            id
        } else {
            ""
        }
    }
}
