package com.gimlelarpes.adskipper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gimlelarpes.adskipper.ui.theme.AdSkipperTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataStoreManager: SettingsDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Datastore and ViewModel
        dataStoreManager = SettingsDataStoreManager(context = this)
        val viewModelFactory = SettingsViewModelFactory(dataStoreManager)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        setContent {
            AdSkipperTheme {
                val navController = rememberNavController()
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