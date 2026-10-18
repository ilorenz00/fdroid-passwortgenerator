package com.lorenzl5.hermes.passwort.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaDataSQLiteHelper

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            androidx.preference.PreferenceFragmentCompat::class.java
                .let {
                    // Simple layout with fragment container
                    android.R.layout.simple_list_item_1
                }
        )
        // Use a FrameLayout container
        val frameLayout = android.widget.FrameLayout(this)
        frameLayout.layoutParams = android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(frameLayout)

        supportFragmentManager.beginTransaction()
            .replace(frameLayout.id, SettingsFragment())
            .commit()

        supportActionBar?.title = getString(R.string.settings_title)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            findPreference<Preference>("delete_all")?.setOnPreferenceClickListener {
                requireContext().let { ctx ->
                    android.app.AlertDialog.Builder(ctx)
                        .setTitle(R.string.delete_confirm_title)
                        .setMessage("Delete ALL accounts? This cannot be undone.")
                        .setPositiveButton(R.string.yes) { _, _ ->
                            MetaDataSQLiteHelper(ctx).deleteAll()
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
                }
                true
            }
        }
    }
}
