package com.gimlelarpes.adskipper

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gimlelarpes.adskipper.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesPage(navController: NavController) {
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
                    IconButton(onClick = { navController.navigate(Routes.SettingsPage) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up_button)
                        )
                    }
                },
            )
        }

        //Top bar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //License entries
            DisplayLicense(R.string.license_adskipper, R.raw.license_adskipper)
            DisplayLicense(R.string.license_fonts, R.raw.license_opensans)
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
            } catch (e: Exception) {
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