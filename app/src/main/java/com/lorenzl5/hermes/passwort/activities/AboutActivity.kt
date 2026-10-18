package com.lorenzl5.hermes.passwort.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lorenzl5.hermes.passwort.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.about_title)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
