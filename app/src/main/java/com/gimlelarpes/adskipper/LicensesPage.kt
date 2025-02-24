package com.gimlelarpes.adskipper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gimlelarpes.adskipper.ui.theme.AdSkipperTheme
import com.gimlelarpes.adskipper.ui.theme.LocalExtendedColorScheme
import com.gimlelarpes.adskipper.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesPage(navController: NavController) {
    val extendedColors = LocalExtendedColorScheme.current
    val scrollState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.open_source_licenses),
                        style = Typography.titleLarge
                        )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomePage) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up_button)
                        )
                    }
                },
            )
        }

        //Top bar
        // TODO: Fix ScrollState not being saved across config changes
    ) { innerPadding ->
        AdSkipperTheme(dynamicColor = false) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(extendedColors.surfaceHighContrast),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //License entries
                items(4) { index ->
                    when (index) {
                        0 -> DisplayLicense(R.string.license_adskipper, R.raw.license_adskipper)
                        1 -> DisplayLicense(R.string.license_fonts, R.raw.license_opensans)
                        2 -> DisplayLicense(R.string.license_aosp, R.raw.license_aosp)
                        3 -> DisplayLicense(R.string.license_kernel, R.raw.license_kernel)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayLicense(header: Int, text: Int) {
    val context = LocalContext.current
    var licenseText by remember { mutableStateOf("") }

    //Launch coroutine
    LaunchedEffect(key1 = text) {
        withContext(Dispatchers.IO) {
            licenseText = try {
                context.resources.openRawResource(text).bufferedReader().use { it.readText() }
            } catch (_: Exception) {
                ""
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = header),
            style = Typography.titleLarge
        )
        Text(
            text = licenseText,
            style = Typography.bodySmall,
            fontFamily = FontFamily.Monospace
        )
    }
}