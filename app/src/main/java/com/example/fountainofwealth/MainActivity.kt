package com.example.fountainofwealth

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fountainofwealth.ui.navigation.AppNavigation
import com.example.fountainofwealth.ui.theme.FountainOfWealthTheme
import com.example.fountainofwealth.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            FountainOfWealthTheme {
                val viewModel: MainViewModel = viewModel()
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}