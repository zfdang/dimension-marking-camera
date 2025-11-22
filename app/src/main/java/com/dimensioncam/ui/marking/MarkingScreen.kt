package com.dimensioncam.ui.marking

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimensioncam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkingScreen(
    viewModel: MarkingViewModel,
    onNavigateBack: () -> Unit
) {
    val photo by viewModel.photo.collectAsState()
    val markings by viewModel.markings.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val selectedMarkingId by viewModel.selectedMarkingId.collectAsState()
    val selectedMarking by viewModel.selectedMarking.collectAsState()
    val isCreatingMarking by viewModel.isCreatingMarking.collectAsState()
    val newMarkingStart by viewModel.newMarkingStart.collectAsState()
    val newMarkingEnd by viewModel.newMarkingEnd.collectAsState()
    val undoStack by viewModel.undoStack.collectAsState()
    
    var showMarkingList by remember { mutableStateOf(false) }
    var showEditPanel by remember { mutableStateOf(false) }
    var showDistanceDialog by remember { mutableStateOf(false) }
    var tempDistance by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_marking)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = undoStack.isNotEmpty()
                    ) {
                        Icon(Icons.Filled.Undo, contentDescription = stringResource(R.string.undo))
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isCreatingMarking) {
                FloatingActionButton(
                    onClick = { viewModel.startCreatingMarking() }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_marking))
                }
            }
        },
        bottomBar = {
            if (!isCreatingMarking && markings.isNotEmpty()) {
                Surface(
                    tonalElevation = 3.dp
                ) {
                    TextButton(
                        onClick = { showMarkingList = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(stringResource(R.string.markings_list) + " (${markings.size})")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            photo?.let { currentPhoto ->
                MarkingCanvas(
                    photoUri = Uri.parse(currentPhoto.originalUri),
                    markings = markings,
                    arrowStyle = settings.arrowStyle,
                    selectedMarkingId = selectedMarkingId,
                    isCreatingMarking = isCreatingMarking,
                    newMarkingStart = newMarkingStart,
                    newMarkingEnd = newMarkingEnd,
                    onMarkingSelected = { markingId ->
                        viewModel.selectMarking(markingId)
                        showEditPanel = true
                    },
                    onNewMarkingStartSet = { x, y ->
                        viewModel.setNewMarkingStart(x, y)
                    },
                    onNewMarkingEndUpdate = { x, y ->
                        viewModel.updateNewMarkingEnd(x, y)
                    },
                    onNewMarkingFinished = {
                        showDistanceDialog = true
                    },
                    onMarkingPointDragged = { markingId, isStart, x, y ->
                        if (isStart) {
                            viewModel.updateMarkingPosition(markingId, startX = x, startY = y)
                        } else {
                            viewModel.updateMarkingPosition(markingId, endX = x, endY = y)
                        }
                    }
                )
            } ?: run {
                Text(
                    text = stringResource(R.string.select_photo),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Marking List Bottom Sheet
        if (showMarkingList) {
            ModalBottomSheet(
                onDismissRequest = { showMarkingList = false }
            ) {
                MarkingList(
                    markings = markings,
                    selectedMarkingId = selectedMarkingId,
                    onMarkingClick = { marking ->
                        viewModel.selectMarking(marking.id)
                        showMarkingList = false
                        showEditPanel = true
                    },
                    onMarkingReorder = { reordered ->
                        viewModel.reorderMarkings(reordered)
                    }
                )
            }
        }
        
        // Edit Panel Bottom Sheet
        if (showEditPanel && selectedMarking != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showEditPanel = false
                    viewModel.selectMarking(null)
                }
            ) {
                MarkingEditPanel(
                    marking = selectedMarking!!,
                    onMarkingUpdate = { updated ->
                        viewModel.updateMarking(updated)
                    },
                    onDelete = {
                        viewModel.deleteMarking(selectedMarking!!.id)
                        showEditPanel = false
                    },
                    onDismiss = {
                        showEditPanel = false
                        viewModel.selectMarking(null)
                    }
                )
            }
        }
        
        // Distance Input Dialog
        if (showDistanceDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDistanceDialog = false
                    viewModel.cancelCreatingMarking()
                },
                title = { Text(stringResource(R.string.distance_label)) },
                text = {
                    Column {
                        Text(
                            text = when (settings.defaultDistanceUnit) {
                                com.dimensioncam.data.model.DistanceUnit.MM -> stringResource(R.string.unit_mm)
                                com.dimensioncam.data.model.DistanceUnit.CM -> stringResource(R.string.unit_cm)
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempDistance,
                            onValueChange = { tempDistance = it },
                            label = { Text(stringResource(R.string.distance_hint)) },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val distance = tempDistance.toFloatOrNull()
                            if (distance != null && distance > 0) {
                                viewModel.finishCreatingMarking(distance)
                                showDistanceDialog = false
                                tempDistance = ""
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDistanceDialog = false
                            viewModel.cancelCreatingMarking()
                            tempDistance = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
