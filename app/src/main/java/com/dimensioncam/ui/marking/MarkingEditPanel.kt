package com.dimensioncam.ui.marking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimensioncam.R
import com.dimensioncam.data.model.Marking

@Composable
fun MarkingEditPanel(
    marking: Marking,
    onMarkingUpdate: (Marking) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var distanceValue by remember(marking) { mutableStateOf(marking.distanceValue.toString()) }
    var lineColor by remember(marking) { mutableStateOf(Color(marking.lineColor)) }
    var lineWidth by remember(marking) { mutableStateOf(marking.lineWidthDp) }
    var textColor by remember(marking) { mutableStateOf(Color(marking.textColor)) }
    var textSize by remember(marking) { mutableStateOf(marking.textSizeSp) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_marking),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Distance
        OutlinedTextField(
            value = distanceValue,
            onValueChange = { distanceValue = it },
            label = { Text(stringResource(R.string.distance_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Line Width
        Text(
            text = "${stringResource(R.string.line_width)}: ${lineWidth.toInt()} dp",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = lineWidth,
            onValueChange = { lineWidth = it },
            valueRange = 1f..10f,
            steps = 8
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Text Size
        Text(
            text = "${stringResource(R.string.text_size)}: ${textSize.toInt()} sp",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = textSize,
            onValueChange = { textSize = it },
            valueRange = 10f..24f,
            steps = 13
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Line Color Picker
        Text(
            text = stringResource(R.string.line_color),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorOption(Color.Red, lineColor == Color.Red) { lineColor = Color.Red }
            ColorOption(Color.Blue, lineColor == Color.Blue) { lineColor = Color.Blue }
            ColorOption(Color.Green, lineColor == Color.Green) { lineColor = Color.Green }
            ColorOption(Color.Yellow, lineColor == Color.Yellow) { lineColor = Color.Yellow }
            ColorOption(Color.Magenta, lineColor == Color.Magenta) { lineColor = Color.Magenta }
            ColorOption(Color.Cyan, lineColor == Color.Cyan) { lineColor = Color.Cyan }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text Color Picker
        Text(
            text = stringResource(R.string.text_color),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorOption(Color.White, textColor == Color.White) { textColor = Color.White }
            ColorOption(Color.Black, textColor == Color.Black) { textColor = Color.Black }
            ColorOption(Color.Yellow, textColor == Color.Yellow) { textColor = Color.Yellow }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete))
            }
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
            
            Button(
                onClick = {
                    val distance = distanceValue.toFloatOrNull()
                    if (distance != null && distance > 0) {
                        onMarkingUpdate(
                            marking.copy(
                                distanceValue = distance,
                                lineColor = lineColor.toArgb(),
                                lineWidthDp = lineWidth,
                                textColor = textColor.toArgb(),
                                textSizeSp = textSize
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = color,
                shape = MaterialTheme.shapes.small
            )
            .then(
                if (isSelected) {
                    Modifier.padding(4.dp)
                } else {
                    Modifier
                }
            )
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
        
        IconButton(
            onClick = onSelect,
            modifier = Modifier.fillMaxSize()
        ) {}
    }
}
