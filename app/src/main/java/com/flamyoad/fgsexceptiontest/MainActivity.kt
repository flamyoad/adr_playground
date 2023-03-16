package com.flamyoad.fgsexceptiontest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.flamyoad.fgsexceptiontest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        with(binding) {
            btnFgs1Instant.setOnClickListener { startFgs1() }
            btnFgs2Instant.setOnClickListener { startFgs2() }
        }
    }

    private fun startFgs1() {
        val intent = Intent(this, FirstForegroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun startFgs2() {
        val intent = Intent(this, SecondForegroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}