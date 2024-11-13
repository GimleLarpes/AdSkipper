package com.gimlelarpes.adskipper

import android.content.res.Configuration
import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gimlelarpes.adskipper.ui.theme.AdSkipperTheme
import com.gimlelarpes.adskipper.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.settings_activity_layout)

        setContent() {
            AdSkipperTheme {
                val viewModel = viewModel<SettingsViewModel>()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        //Main column layout
                        Column(
                            modifier = Modifier.fillMaxSize().weight(1f, false),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Logo
                            val configuration = LocalConfiguration.current
                            Box(
                                modifier = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                                    Modifier.fillMaxSize(0.5f).offset(y = 50.dp) else Modifier.fillMaxHeight(0.5f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.padding(20.dp).fillMaxSize()//Modifier.size(280.dp)
                                )
                            }

                            //Title
                            Text(text = "AdSkipper", style = Typography.displayMedium)
                            Spacer(modifier = Modifier.height(Typography.displayMedium.fontSize.value.dp / 2))

                            //Adskip block
                            Column(
                                //modifier = Modifier
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AdSkipSwitch()
                                val switch_text = if (viewModel.adSkipEnabled.value) {//WIP
                                    stringResource(R.string.ad_skip_enabled)
                                } else {
                                    stringResource(R.string.ad_skip_disabled)
                                }
                                Text(
                                    switch_text,
                                    style = Typography.labelSmall,
                                    modifier = Modifier.offset(y = -Typography.labelSmall.lineHeight.value.dp / 3)
                                )
                            }
                        }

                        //Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { viewModel.showLicenses() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.onBackground)
                            ) {
                                Text(text = stringResource(R.string.open_source_licenses)
                                    , style = Typography.labelSmall)
                            }
                        }
                    }
                }
            }
       }
    }
}


@Composable
fun AdSkipSwitch() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val viewModel = viewModel<SettingsViewModel>()
        val typeFace = Typography.titleLarge
        Text(
            text = stringResource(R.string.ad_skip_switch_text),
            style = typeFace,
            modifier = Modifier.padding(typeFace.fontSize.value.dp / 2)
        )
        Switch(
            checked = viewModel.adSkipEnabled.value,
            onCheckedChange = { viewModel.toggleAdSkip() }
        )
    }
}