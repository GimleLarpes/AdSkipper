package com.gimlelarpes.adskipper

import android.R
import android.os.Bundle
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gimlelarpes.adskipper.ui.theme.AdSkipperTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.settings_activity_layout)

        setContent() {
            AdSkipperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Switch(checked = viewModel.adSkipEnabled.value, onCheckedChange = { viewModel.toggleAdSkip() })

                    val togglestring = if (viewModel.adSkipEnabled.value) {//WIP
                        "AdSkip is enabled"
                    } else {
                        "AdSkip is disabled"
                    }
                    Text(togglestring)
                    //Greeting(
                    //    name = "Android",
                    //    modifier = Modifier.padding(innerPadding)
                    //)
                }
            }
       }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdSkipperTheme {
        Greeting("Android")
    }
}