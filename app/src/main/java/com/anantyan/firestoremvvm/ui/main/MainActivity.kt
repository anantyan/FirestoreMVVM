package com.anantyan.firestoremvvm.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.anantyan.firestoremvvm.R
import com.anantyan.firestoremvvm.databinding.ActivityMainBinding
import com.anantyan.firestoremvvm.utils.Resource
import com.anantyan.firestoremvvm.utils.onSnackError
import com.anantyan.firestoremvvm.utils.onSnackSuccess
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
            if (it is Resource.Success) {
                this.onSnackSuccess(binding.root, "Berhasil ditulis, ampun suhu 🙏")
            }
        }
        viewModel.delete.observe(this) {
            if (it is Resource.Success) {
                this.onSnackSuccess(binding.root, "Berhasil dihapus, ampun suhu 🙏")
            }
        }
        viewModel.read.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.error.toString() + ", ampun suhu 🙏")
            }
        }

        viewModel.write.observe(this) {
            if (it is Resource.Error) {
                this.onSnackError(binding.root, it.error.toString() + ", ampun suhu 🙏")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}