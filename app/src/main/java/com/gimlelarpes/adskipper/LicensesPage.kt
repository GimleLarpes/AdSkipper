package com.gimlelarpes.adskipper

import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gimlelarpes.adskipper.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesPage(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                //colors = TopAppBarColors(
                //    containerColor = MaterialTheme.colorScheme.primaryContainer,
                //    titleContentColor = MaterialTheme.colorScheme.primary,
                //),
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
        //Textview
    }
    //Display License.txt in scaffholding framework
}