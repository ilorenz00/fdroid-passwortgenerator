package com.lorenzl5.hermes.passwort.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.helpers.PreferenceKeys

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Redirect: first launch -> Tutorial, else Main
        val intent = if (!PreferenceKeys.isTutorialDone(this)) {
            Intent(this, TutorialActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
