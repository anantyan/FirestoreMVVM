package com.example.main.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.common.utils.Resource
import com.example.common.utils.onSnackError
import com.example.common.utils.onSnackSuccess
import com.example.main.R
import com.example.main.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBindObserver()
        onBindView()
    }

    private fun onBindView() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        navController = navHost.navController
        NavigationUI.setupWithNavController(binding.toolbar, navController)
    }

    private fun onBindObserver() {
        viewModel.write.observe(this) {
            when (it) {
                is Resource.Success -> {
                    this.onSnackSuccess(binding.root, "Berhasil ditulis, ampun suhu 🙏")
                }
                is Resource.Error -> {
                    this.onSnackError(binding.root, it.exception + ", ampun suhu 🙏")
                }
                else -> {}
            }
        }
        viewModel.delete.observe(this) {
            if (it is Resource.Success) {
                this.onSnackSuccess(binding.root, "Berhasil dihapus, ampun suhu 🙏")
            }
        }
        viewModel.read.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.exception + ", ampun suhu 🙏")
            }
        }
        viewModel.currentLocation.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.exception + ", ampun suhu 🙏")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}