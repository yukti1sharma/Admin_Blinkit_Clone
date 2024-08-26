package com.example.adminblinkitclone.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adminblinkitclone.R

class AuthMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }
}