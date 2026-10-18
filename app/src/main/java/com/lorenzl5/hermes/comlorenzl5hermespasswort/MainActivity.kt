package com.lorenzl5.hermes.comlorenzl5hermespasswort

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lorenzl5.hermes.comlorenzl5hermespasswort.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
