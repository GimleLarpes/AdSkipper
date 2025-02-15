package com.gimlelarpes.adskipper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gimlelarpes.adskipper.ui.theme.AdSkipperTheme

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: SettingsDataStoreManager
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        dataStoreManager = SettingsDataStoreManager(context = this)


        enableEdgeToEdge()
        setContent {
            AdSkipperTheme {
                val navController = rememberNavController()
                val viewModelFactory = SettingsViewModelFactory(dataStoreManager, application)
                viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

                NavHost(
                    navController = navController,
                    startDestination = Routes.SettingsPage,
                    builder = {
                        composable(Routes.SettingsPage) {
                            SettingsPage(navController, viewModel)
                        }
                        composable(Routes.LicensesPage) {
                            LicensesPage(navController)
                        }
                    }
                )
            }
       }
    }
}