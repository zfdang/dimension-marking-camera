package com.zfdang.dimensioncam.ui.photos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.data.Photo;
import com.zfdang.dimensioncam.data.PhotoWithAnnotations;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<PhotoWithAnnotations> photos = new ArrayList<>();
    private Context context;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
        void onDeleteClick(Photo photo);
        void onExportClick(Photo photo);
    }

    public PhotoAdapter(Context context, OnPhotoClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setPhotos(List<PhotoWithAnnotations> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoWithAnnotations item = photos.get(position);
        Photo photo = item.photo;
        
        Glide.with(context)
                .load(photo.originalPath)
                .transform(new CenterCrop(), new AnnotationTransformation(context, item.annotations))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(photo));
        holder.exportButton.setOnClickListener(v -> listener.onExportClick(photo));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;
        ImageButton exportButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            exportButton = itemView.findViewById(R.id.btn_export);
        }
    }
}
