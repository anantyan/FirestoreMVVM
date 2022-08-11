package com.anantyan.firestoremvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.anantyan.firestoremvvm.R
import com.anantyan.firestoremvvm.databinding.ActivityMainBinding
import com.anantyan.firestoremvvm.utils.Resource
import com.anantyan.firestoremvvm.utils.onSnackError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBindObserver()
    }

    private fun onBindObserver() {
        viewModel.read.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.error.toString())
            }
        }

        viewModel.write.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.error.toString())
            }
        }
    }
}