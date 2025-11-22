package com.dimensioncam.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimensioncam.BuildConfig
import com.dimensioncam.R
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.DistanceUnit

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    
    var showArrowStyleDialog by remember { mutableStateOf(false) }
    var showDistanceUnitDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_settings)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Arrow Style
            SettingSection(title = stringResource(R.string.arrow_style)) {
                SettingItem(
                    label = stringResource(R.string.arrow_style),
                    value = when (settings.arrowStyle) {
                        ArrowStyle.ARROW -> stringResource(R.string.arrow_style_arrow)
                        ArrowStyle.T_CAP -> stringResource(R.string.arrow_style_t_cap)
                        ArrowStyle.CIRCLE -> stringResource(R.string.arrow_style_circle)
                    },
                    onClick = { showArrowStyleDialog = true }
                )
            }
            
            Divider()
            
            // Distance Unit
            SettingSection(title = stringResource(R.string.default_unit)) {
                SettingItem(
                    label = stringResource(R.string.default_unit),
                    value = when (settings.defaultDistanceUnit) {
                        DistanceUnit.MM -> stringResource(R.string.unit_mm)
                        DistanceUnit.CM -> stringResource(R.string.unit_cm)
                    },
                    onClick = { showDistanceUnitDialog = true }
                )
            }
            
            Divider()
            
            // Language
            SettingSection(title = stringResource(R.string.language)) {
                SettingItem(
                    label = stringResource(R.string.language),
                    value = when (settings.languageCode) {
                        "auto" -> stringResource(R.string.language_auto)
                        "en" -> stringResource(R.string.language_en)
                        "zh" -> stringResource(R.string.language_zh)
                        else -> stringResource(R.string.language_auto)
                    },
                    onClick = { showLanguageDialog = true }
                )
            }
            
            Divider()
            
            // Version Info
            SettingSection(title = stringResource(R.string.version_info)) {
                SettingItem(
                    label = stringResource(R.string.version),
                    value = BuildConfig.VERSION_NAME,
                    onClick = null
                )
            }
            
            Divider()
            
            // GitHub Info
            SettingSection(title = stringResource(R.string.github_info)) {
                SettingItem(
                    label = stringResource(R.string.github_link),
                    value = "github.com/yourusername/dimension-cam",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://github.com/yourusername/dimension-cam")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            Divider()
            
            // Author Info
            SettingSection(title = stringResource(R.string.author_info)) {
                SettingItem(
                    label = stringResource(R.string.author_name),
                    value = "DimensionCam Team",
                    onClick = null
                )
            }
        }
        
        // Arrow Style Dialog
        if (showArrowStyleDialog) {
            AlertDialog(
                onDismissRequest = { showArrowStyleDialog = false },
                title = { Text(stringResource(R.string.arrow_style)) },
                text = {
                    Column {
                        ArrowStyle.values().forEach { style ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateArrowStyle(style)
                                        showArrowStyleDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.arrowStyle == style,
                                    onClick = {
                                        viewModel.updateArrowStyle(style)
                                        showArrowStyleDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (style) {
                                        ArrowStyle.ARROW -> stringResource(R.string.arrow_style_arrow)
                                        ArrowStyle.T_CAP -> stringResource(R.string.arrow_style_t_cap)
                                        ArrowStyle.CIRCLE -> stringResource(R.string.arrow_style_circle)
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showArrowStyleDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // Distance Unit Dialog
        if (showDistanceUnitDialog) {
            AlertDialog(
                onDismissRequest = { showDistanceUnitDialog = false },
                title = { Text(stringResource(R.string.default_unit)) },
                text = {
                    Column {
                        DistanceUnit.values().forEach { unit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateDefaultDistanceUnit(unit)
                                        showDistanceUnitDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.defaultDistanceUnit == unit,
                                    onClick = {
                                        viewModel.updateDefaultDistanceUnit(unit)
                                        showDistanceUnitDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (unit) {
                                        DistanceUnit.MM -> stringResource(R.string.unit_mm)
                                        DistanceUnit.CM -> stringResource(R.string.unit_cm)
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showDistanceUnitDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // Language Dialog
        if (showLanguageDialog) {
            val languages = listOf("auto" to R.string.language_auto, "en" to R.string.language_en, "zh" to R.string.language_zh)
            
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text(stringResource(R.string.language)) },
                text = {
                    Column {
                        languages.forEach { (code, nameRes) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateLanguageCode(code, context)
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.languageCode == code,
                                    onClick = {
                                        viewModel.updateLanguageCode(code, context)
                                        showLanguageDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(nameRes))
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        content()
    }
}

@Composable
fun SettingItem(
    label: String,
    value: String,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (onClick != null) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
