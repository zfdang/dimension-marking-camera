package com.zfdang.dimensioncam.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.ui.MainActivity;

public class SettingsFragment extends Fragment {

    private SettingsManager settingsManager;
    private TextView tvArrowStyle;
    private TextView tvLanguage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsManager = new SettingsManager(getContext());

        tvArrowStyle = view.findViewById(R.id.tv_arrow_style_value);
        tvLanguage = view.findViewById(R.id.tv_language_value);

        view.findViewById(R.id.ll_arrow_style).setOnClickListener(v -> showArrowStyleDialog());
        view.findViewById(R.id.ll_language).setOnClickListener(v -> showLanguageDialog());

        updateUI();
        return view;
    }

    private void updateUI() {
        int style = settingsManager.getArrowStyle();
        if (style == SettingsManager.STYLE_ARROW) tvArrowStyle.setText(R.string.style_arrow);
        else if (style == SettingsManager.STYLE_T_SHAPE) tvArrowStyle.setText(R.string.style_t_shape);
        else if (style == SettingsManager.STYLE_DOT) tvArrowStyle.setText(R.string.style_dot);

        String lang = settingsManager.getLanguage();
        if ("auto".equals(lang)) tvLanguage.setText("Auto");
        else if ("en".equals(lang)) tvLanguage.setText("English");
        else if ("zh".equals(lang)) tvLanguage.setText("中文");
    }

    private void showArrowStyleDialog() {
        String[] items = {getString(R.string.style_arrow), getString(R.string.style_t_shape), getString(R.string.style_dot)};
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_arrow_style)
                .setItems(items, (dialog, which) -> {
                    settingsManager.setArrowStyle(which);
                    updateUI();
                })
                .show();
    }

    private void showLanguageDialog() {
        String[] items = {"Auto", "English", "中文"};
        String[] values = {"auto", "en", "zh"};
        
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_language)
                .setItems(items, (dialog, which) -> {
                    settingsManager.setLanguage(values[which]);
                    updateUI();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).restart();
                    }
                })
                .show();
    }
}
