package com.gimlelarpes.adskipper

import android.icu.text.DecimalFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gimlelarpes.adskipper.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

val interactionSource = MutableInteractionSource()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController, viewModel: SettingsViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title),
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Main column layout
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Title
                val typeFace = Typography.displayMedium
                Column(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = typeFace
                    )
                }
                Spacer(modifier = Modifier.height(typeFace.fontSize.value.dp / 2))

                //Settings block
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f, false),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Settings
                    SettingsEntry(MuteSwitch(viewModel), R.string.ad_skip_mute_ads_description)
                    SettingsEntry(NotifTimeoutSlider(viewModel), R.string.ad_skip_notif_timeout_description)
                }
            }
        }
    }
}

@Composable
fun SettingsEntry(entry: Unit, description: Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        entry
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(description),
                style = Typography.labelSmall,
                modifier = Modifier
                    .offset(y = -Typography.labelSmall.lineHeight.value.dp / 3)
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(Typography.titleLarge.lineHeight.value.dp / 2))
    }
}

@Composable
fun MuteSwitch(viewModel: SettingsViewModel = viewModel()) {
    val isAdMuteEnabled by viewModel.isAdMuteEnabledFlow.collectAsStateWithLifecycle(initialValue = true)
    val coroutineScope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typeFace = Typography.titleLarge

        Text(
            text = stringResource(R.string.ad_skip_mute_ads),
            style = typeFace,
            modifier = Modifier.padding(typeFace.fontSize.value.dp / 2)
        )
        Switch(
            checked = isAdMuteEnabled,
            onCheckedChange = { newChecked ->
                coroutineScope.launch {
                    viewModel.setEnableAdMute(newChecked)
                }
            },
            thumbContent = if (isAdMuteEnabled) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifTimeoutSlider(viewModel: SettingsViewModel = viewModel()) {
    val notificationTimeout by viewModel.notificationTimeoutFlow.collectAsStateWithLifecycle(initialValue = 100)

    // Define ranges
    val positionRange: ClosedFloatingPointRange<Float> = 0f..10f
    val timeoutRange: LongRange = LongRange(10, 1000)

    var sliderPosition = getFloatFromTimeout(notificationTimeout, timeoutRange = timeoutRange, positionRange = positionRange)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Setting text
        val typeFace = Typography.titleLarge
        Text(
            text = stringResource(R.string.ad_skip_notif_timeout),
            style = typeFace,
            modifier = Modifier
                .offset(y = Typography.labelSmall.lineHeight.value.dp / 3)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            // Slider
            Slider(
                modifier = Modifier.wrapContentSize()
                    .weight(3f, false),
                value = sliderPosition,
                onValueChange = {
                    viewModel.setNotificationTimeout(
                        getTimeoutFromFloat(
                            it,
                            timeoutRange = timeoutRange,
                            positionRange = positionRange
                        )
                    )
                },
                steps = 5,
                valueRange = positionRange,
                interactionSource = interactionSource,
                thumb = {
                    SliderDefaults.Thumb(
                        modifier = Modifier.scale(scaleX = 1f, scaleY = 0.5f),
                        interactionSource = interactionSource,
                    )
                }
            )

            // Polling rate text
            var dispPollRate = 1000.0 / notificationTimeout
            val df: DecimalFormat = if (dispPollRate < 2) DecimalFormat("#.#") else DecimalFormat("#")

            Text(
                modifier = Modifier.padding(horizontal = typeFace.fontSize.value.dp / 2)
                    .weight(1f, false),
                text = df.format(dispPollRate)+" Hz",
            )
        }
    }
}

// Maps between Float and Long, in ranges positionRange and timeoutrange
fun getTimeoutFromFloat(value: Float, timeoutRange: LongRange, positionRange: ClosedFloatingPointRange<Float>): Long {
    var relval = (value - positionRange.start) / (positionRange.endInclusive - positionRange.start)
    relval = 1 - relval // Invert
    return (relval.pow(2) * (timeoutRange.endInclusive - timeoutRange.start)).roundToLong() + timeoutRange.start
}
fun getFloatFromTimeout(value: Long, timeoutRange: LongRange, positionRange: ClosedFloatingPointRange<Float>): Float {
    var relval = (value - timeoutRange.start).toFloat() / (timeoutRange.endInclusive - timeoutRange.start).toFloat()
    // Invert
    return positionRange.endInclusive - sqrt(relval) * (positionRange.endInclusive - positionRange.start) + positionRange.start
}