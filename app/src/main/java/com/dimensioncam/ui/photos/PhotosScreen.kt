package com.dimensioncam.ui.photos

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.dimensioncam.R
import com.dimensioncam.data.model.Photo
import com.dimensioncam.utils.FileManager
import com.dimensioncam.utils.ImageExporter
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    onPhotoClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()
    
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Camera capture
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri.value != null) {
            viewModel.addPhoto(cameraImageUri.value!!)
        }
    }
    
    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addPhoto(it) }
    }
    
    // Camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context, cameraImageUri, cameraLauncher)
        }
    }
    
    // Storage permission for older Android versions
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPhotoDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_photo))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (photos.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Photo,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.photos_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Photo grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos, key = { it.id }) { photo ->
                        PhotoItem(
                            photo = photo,
                            onClick = { onPhotoClick(photo.id) },
                            onLongClick = { selectedPhoto = photo },
                            onExport = {
                                scope.launch {
                                    exportPhoto(context, photo, viewModel, settings.arrowStyle)
                                }
                            }
                        )
                    }
                }
            }
        }
        
        // Add Photo Dialog
        if (showAddPhotoDialog) {
            AlertDialog(
                onDismissRequest = { showAddPhotoDialog = false },
                title = { Text(stringResource(R.string.add_photo)) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                showAddPhotoDialog = false
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    launchCamera(context, cameraImageUri, cameraLauncher)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.take_photo))
                        }
                        
                        TextButton(
                            onClick = {
                                showAddPhotoDialog = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Photo, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.choose_from_gallery))
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAddPhotoDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // Photo Options Dialog (Long Press)
        if (selectedPhoto != null) {
            AlertDialog(
                onDismissRequest = { selectedPhoto = null },
                title = { Text(stringResource(R.string.app_name)) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    exportPhoto(context, selectedPhoto!!, viewModel, settings.arrowStyle)
                                }
                                selectedPhoto = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.export_photo))
                        }
                        
                        TextButton(
                            onClick = {
                                showDeleteDialog = true
                                selectedPhoto = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(R.string.delete_photo),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { selectedPhoto = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog && selectedPhoto != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_photo)) },
                text = { Text(stringResource(R.string.delete_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedPhoto?.let { viewModel.deletePhoto(it) }
                            showDeleteDialog = false
                            selectedPhoto = null
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // Export Snackbar
        when (val status = exportStatus) {
            is ExportStatus.Success -> {
                LaunchedEffect(status) {
                    // Show success message
                    viewModel.resetExportStatus()
                }
            }
            is ExportStatus.Error -> {
                LaunchedEffect(status) {
                    viewModel.resetExportStatus()
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onExport: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AsyncImage(
            model = Uri.parse(photo.originalUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun launchCamera(
    context: android.content.Context,
    cameraImageUri: MutableState<Uri?>,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>
) {
    val fileManager = FileManager(context)
    val file = fileManager.createTempImageFile()
    val uri = fileManager.getUriForFile(file)
    cameraImageUri.value = uri
    cameraLauncher.launch(uri)
}

private suspend fun exportPhoto(
    context: android.content.Context,
    photo: Photo,
    viewModel: PhotosViewModel,
    arrowStyle: com.dimensioncam.data.model.ArrowStyle
) {
    try {
        val exporter = ImageExporter(context)
        val photoRepository = (context.applicationContext as com.dimensioncam.DimensionCamApplication).photoRepository
        
        // Get markings for this photo
        val markings = photoRepository.getMarkingsForPhotoSync(photo.id)
        
        // Generate filename
        val originalFileName = Uri.parse(photo.originalUri).lastPathSegment ?: "photo"
        val fileName = if (originalFileName.endsWith(".jpg", ignoreCase = true) ||
            originalFileName.endsWith(".jpeg", ignoreCase = true)
        ) {
            originalFileName.replace(".jpg", "_marked.jpg", ignoreCase = true)
                .replace(".jpeg", "_marked.jpg", ignoreCase = true)
        } else {
            "${originalFileName}_marked.jpg"
        }
        
        // Export
        val uri = exporter.exportMarkedImage(
            photoUri = Uri.parse(photo.originalUri),
            markings = markings,
            arrowStyle = arrowStyle,
            fileName = fileName
        )
        
        // TODO: Show success/error toast
    } catch (e: Exception) {
        e.printStackTrace()
        // TODO: Show error toast
    }
}
