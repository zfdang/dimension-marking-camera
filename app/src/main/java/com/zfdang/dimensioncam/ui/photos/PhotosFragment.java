package com.zfdang.dimensioncam.ui.photos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.data.Annotation;
import com.zfdang.dimensioncam.data.AppDatabase;
import com.zfdang.dimensioncam.data.Photo;
import com.zfdang.dimensioncam.ui.MainActivity;
import com.zfdang.dimensioncam.ui.annotation.AnnotationDrawer;
import com.zfdang.dimensioncam.ui.settings.SettingsManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotosFragment extends Fragment implements PhotoAdapter.OnPhotoClickListener {

    private PhotosViewModel photosViewModel;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private Uri currentPhotoUri;
    private SettingsManager settingsManager;
    private TextView emptyHint;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (currentPhotoUri != null) {
                        Photo photo = new Photo(currentPhotoUri.toString(), System.currentTimeMillis());
                        photosViewModel.insert(photo);
                    }
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        getContext().getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        // This is expected for some content providers (e.g., Google Photos)
                        // The URI will still work for one-time access
                        android.util.Log.d("PhotosFragment", "Cannot persist URI permission: " + e.getMessage());
                    }
                    Photo photo = new Photo(uri.toString(), System.currentTimeMillis());
                    photosViewModel.insert(photo);
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        settingsManager = new SettingsManager(getContext());

        recyclerView = view.findViewById(R.id.rv_photos);
        emptyHint = view.findViewById(R.id.tv_empty_hint);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        photosViewModel = new ViewModelProvider(this).get(PhotosViewModel.class);
        photosViewModel.getAllPhotos().observe(getViewLifecycleOwner(), photos -> {
            adapter.setPhotos(photos);
            if (photos == null || photos.isEmpty()) {
                emptyHint.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyHint.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photos, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_photo) {
            showAddPhotoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT)
                            .show();
                }
            });

    private void showAddPhotoDialog() {
        String[] options = { getString(R.string.source_camera), getString(R.string.source_gallery) };
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.action_add_photo)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndDispatch();
                    } else {
                        pickImageLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndDispatch() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        if (getContext() != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                currentPhotoUri = FileProvider.getUriForFile(getContext(),
                        "com.zfdang.dimensioncam.fileprovider",
                        photoFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                takePictureLauncher.launch(intent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir("Pictures");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onPhotoClick(Photo photo) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToAnnotation(photo.id);
        }
    }

    @Override
    public void onDeleteClick(Photo photo) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    photosViewModel.delete(photo);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onExportClick(Photo photo) {
        // Run on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                exportPhoto(photo);
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(
                            () -> Toast.makeText(getContext(), R.string.error_save_failed, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void exportPhoto(Photo photo) throws IOException {
        // 1. Load original bitmap
        Uri uri = Uri.parse(photo.originalPath);
        InputStream is = getContext().getContentResolver().openInputStream(uri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(is);
        is.close();

        if (originalBitmap == null)
            return;

        // 2. Create mutable bitmap for drawing
        Bitmap resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(resultBitmap);

        // 3. Get annotations
        List<Annotation> annotations = AppDatabase.getDatabase(getContext()).annotationDao()
                .getAnnotationsForPhotoSync(photo.id);

        // 4. Draw
        AnnotationDrawer drawer = new AnnotationDrawer(getContext());
        RectF rect = new RectF(0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());
        int arrowStyle = settingsManager.getArrowStyle();

        // Calculate scale factor relative to a standard screen width
        // Use square root scaling to prevent text/lines from becoming too large on
        // high-res photos
        // For 1080px: factor=1.0, 2160px: factor=1.41, 4320px: factor=2.0
        float ratio = resultBitmap.getWidth() / com.zfdang.dimensioncam.utils.Constants.STANDARD_SCREEN_WIDTH;
        float scaleFactor = (float) Math.pow(ratio, 0.7);
        // Limit scale factor to user-configured range [1.0, maxScaleFactor]
        float maxScaleFactor = settingsManager.getMaxScaleFactor();
        scaleFactor = Math.max(1f, Math.min(scaleFactor, maxScaleFactor));

        drawer.draw(canvas, annotations, rect, arrowStyle, false, scaleFactor, false);

        // 5. Save to Gallery
        saveBitmapToGallery(resultBitmap);

        // Cleanup
        originalBitmap.recycle();
        resultBitmap.recycle();

        if (getActivity() != null) {
            getActivity().runOnUiThread(
                    () -> Toast.makeText(getContext(), R.string.msg_saved_to_gallery, Toast.LENGTH_SHORT).show());
        }
    }

    private void saveBitmapToGallery(Bitmap bitmap) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "DimensionCam_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DimensionCam");
        }

        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream os = resolver.openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            }
        }
    }
}
