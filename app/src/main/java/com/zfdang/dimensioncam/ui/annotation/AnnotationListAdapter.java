package com.zfdang.dimensioncam.ui.annotation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.data.Annotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotationListAdapter extends RecyclerView.Adapter<AnnotationListAdapter.ViewHolder> {

    private List<Annotation> annotations = new ArrayList<>();
    private OnAnnotationActionListener listener;

    public interface OnAnnotationActionListener {
        void onAnnotationClick(Annotation annotation);
        void onDeleteClick(Annotation annotation);
    }

    public AnnotationListAdapter(OnAnnotationActionListener listener) {
        this.listener = listener;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        notifyDataSetChanged();
    }
    
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_annotation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Annotation annotation = annotations.get(position);
        holder.tvValue.setText(String.format("%.1f", annotation.measuredValue));
        holder.colorIndicator.setBackgroundColor(annotation.color);
        
        holder.itemView.setOnClickListener(v -> listener.onAnnotationClick(annotation));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(annotation));
    }

    @Override
    public int getItemCount() {
        return annotations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValue;
        View colorIndicator;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            colorIndicator = itemView.findViewById(R.id.view_color);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
