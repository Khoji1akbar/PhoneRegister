package com.example.phoneregister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.phoneregister.databinding.ActivityMain3Binding
import com.example.phoneregister.databinding.ActivityMainBinding

class MainActivity3 : AppCompatActivity() {
    private val binding by lazy { ActivityMain3Binding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}