package com.zfdang.dimensioncam.ui.annotation;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.data.Annotation;

import java.util.Collections;
import java.util.List;

public class AnnotationFragment extends Fragment implements AnnotationListAdapter.OnAnnotationActionListener, DrawView.OnAnnotationChangeListener {

    private AnnotationViewModel viewModel;
    private PhotoView photoView;
    private DrawView drawView;
    private RecyclerView recyclerView;
    private AnnotationListAdapter adapter;
    private TextView emptyHint;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);

        photoView = view.findViewById(R.id.photo_view);
        drawView = view.findViewById(R.id.draw_view);
        drawView.setPhotoView(photoView);
        drawView.setListener(this);

        emptyHint = view.findViewById(R.id.tv_empty_hint);

        recyclerView = view.findViewById(R.id.rv_annotations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnnotationListAdapter(this);
        recyclerView.setAdapter(adapter);

        // Drag and drop
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                List<Annotation> list = adapter.getAnnotations();
                Collections.swap(list, fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No swipe to delete
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.reorderAnnotations(adapter.getAnnotations());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(this).get(AnnotationViewModel.class);
        viewModel.getCurrentPhoto().observe(getViewLifecycleOwner(), photo -> {
            if (photo != null) {
                emptyHint.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                drawView.setVisibility(View.VISIBLE);
                Glide.with(this)
                    .load(photo.originalPath)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            drawView.post(() -> drawView.invalidate());
                            return false;
                        }
                    })
                    .into(photoView);
            } else {
                emptyHint.setVisibility(View.VISIBLE);
                photoView.setVisibility(View.GONE);
                drawView.setVisibility(View.GONE);
            }
        });

        viewModel.getAnnotations().observe(getViewLifecycleOwner(), annotations -> {
            drawView.setAnnotations(annotations);
            adapter.setAnnotations(annotations);
            getActivity().invalidateOptionsMenu(); // Update undo button state
        });
        
        // PhotoView zoom listener to invalidate DrawView
        photoView.setOnMatrixChangeListener(rect -> drawView.invalidate());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_annotation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem undoItem = menu.findItem(R.id.action_undo);
        if (undoItem != null && viewModel != null) {
            undoItem.setEnabled(viewModel.canUndo());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add_annotation) {
            addNewAnnotation();
            return true;
        } else if (itemId == R.id.action_undo) {
            viewModel.undo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadPhoto(long photoId) {
        if (viewModel != null) {
            viewModel.loadPhoto(photoId);
        }
    }

    private void addNewAnnotation() {
        if (viewModel.getCurrentPhoto().getValue() == null) return;
        long photoId = viewModel.getCurrentPhoto().getValue().id;
        // Default center
        Annotation annotation = new Annotation(photoId, 0.2f, 0.5f, 0.8f, 0.5f, 0f, Color.RED, 5f, adapter.getItemCount());
        viewModel.addAnnotation(annotation);
    }

    @Override
    public void onAnnotationClick(Annotation annotation) {
        showPropertiesDialog(annotation);
    }

    @Override
    public void onDeleteClick(Annotation annotation) {
        viewModel.deleteAnnotation(annotation);
    }

    @Override
    public void onAnnotationModified(Annotation annotation) {
        viewModel.updateAnnotation(annotation);
    }

    @Override
    public void onAnnotationSelected(Annotation annotation) {
        // Optional: highlight in list?
    }

    private void showPropertiesDialog(Annotation annotation) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_annotation_properties, null);
        EditText etDistance = view.findViewById(R.id.et_distance);
        RadioGroup rgColor = view.findViewById(R.id.rg_color);
        SeekBar sbWidth = view.findViewById(R.id.sb_width);

        etDistance.setText(String.valueOf(annotation.measuredValue));
        
        if (annotation.color == Color.RED) rgColor.check(R.id.rb_red);
        else if (annotation.color == Color.GREEN) rgColor.check(R.id.rb_green);
        else if (annotation.color == Color.BLUE) rgColor.check(R.id.rb_blue);
        else if (annotation.color == Color.YELLOW) rgColor.check(R.id.rb_yellow);
        else rgColor.check(R.id.rb_red); // Default

        sbWidth.setProgress((int) annotation.width);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_annotation_properties)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    try {
                        float val = Float.parseFloat(etDistance.getText().toString());
                        annotation.measuredValue = val;
                    } catch (NumberFormatException e) {
                        // ignore
                    }

                    int id = rgColor.getCheckedRadioButtonId();
                    if (id == R.id.rb_red) annotation.color = Color.RED;
                    else if (id == R.id.rb_green) annotation.color = Color.GREEN;
                    else if (id == R.id.rb_blue) annotation.color = Color.BLUE;
                    else if (id == R.id.rb_yellow) annotation.color = Color.YELLOW;

                    annotation.width = sbWidth.getProgress();
                    if (annotation.width < 1) annotation.width = 1;

                    viewModel.updateAnnotation(annotation);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
