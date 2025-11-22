package com.dimensioncam.ui.marking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimensioncam.R
import com.dimensioncam.data.model.Marking
import org.burnoutcrew.reorderable.*

@Composable
fun MarkingList(
    markings: List<Marking>,
    selectedMarkingId: Long?,
    onMarkingClick: (Marking) -> Unit,
    onMarkingReorder: (List<Marking>) -> Unit
) {
    var items by remember(markings) { mutableStateOf(markings) }
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            items = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        },
        onDragEnd = { _, _ ->
            onMarkingReorder(items)
        }
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.markings_list),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = stringResource(R.string.drag_to_reorder),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            state = reorderableState.listState,
            modifier = Modifier
                .fillMaxWidth()
                .reorderable(reorderableState)
        ) {
            items(items, key = { it.id }) { marking ->
                ReorderableItem(reorderableState, key = marking.id) { isDragging ->
                    val elevation = if (isDragging) 4.dp else 0.dp
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onMarkingClick(marking) },
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = formatDistance(marking),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Order: ${marking.displayOrder + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Color indicator
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(marking.lineColor),
                                    shape = MaterialTheme.shapes.small
                                ) {}
                            }
                            
                            // Drag handle
                            Icon(
                                Icons.Filled.DragHandle,
                                contentDescription = null,
                                modifier = Modifier.detectReorderAfterLongPress(reorderableState)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDistance(marking: Marking): String {
    val unit = if (marking.distanceUnit.name == "MM") "mm" else "cm"
    return "%.1f %s".format(marking.distanceValue, unit)
}
