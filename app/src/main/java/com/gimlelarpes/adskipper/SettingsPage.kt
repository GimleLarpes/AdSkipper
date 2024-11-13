package com.gimlelarpes.adskipper

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gimlelarpes.adskipper.ui.theme.Typography
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@Composable
fun SettingsPage(navController: NavController) {
    val viewModel = viewModel<SettingsViewModel>()
    Scaffold(
        modifier = Modifier.fillMaxSize()
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
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(20.dp).fillMaxSize()
                    )
                }

                //Title
                Text(text = "AdSkipper", style = Typography.displayMedium)
                Spacer(modifier = Modifier.height(Typography.displayMedium.fontSize.value.dp / 2))

                //AdSkip switch block
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AdSkipSwitch()
                    val isServiceEnabled = viewModel.adSkipEnabled.collect()
                    val statetext = if (isServiceEnabled) {
                        stringResource(R.string.ad_skip_enabled)
                    } else {
                        stringResource(R.string.ad_skip_disabled)
                    }
                    Text(
                        text = statetext,
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
                    onClick = { navController.navigate(Routes.LicensesPage) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(text = stringResource(R.string.open_source_licenses),
                         style = Typography.labelSmall)
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
        val checked = viewModel.adSkipEnabled
        Switch(
            checked = checked,
            onCheckedChange = { viewModel.toggleAdSkip() },
            thumbContent = if (checked) {
                {
                    Icon(imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            } else null
        )
    }
}