package com.zfdang.dimensioncam.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private TextView tvMaxScaleFactor;
    private TextView tvVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsManager = new SettingsManager(getContext());

        tvArrowStyle = view.findViewById(R.id.tv_arrow_style_value);
        tvLanguage = view.findViewById(R.id.tv_language_value);
        tvMaxScaleFactor = view.findViewById(R.id.tv_max_scale_factor_value);
        tvVersion = view.findViewById(R.id.tv_version_value);

        view.findViewById(R.id.ll_arrow_style).setOnClickListener(v -> showArrowStyleDialog());
        view.findViewById(R.id.ll_language).setOnClickListener(v -> showLanguageDialog());
        view.findViewById(R.id.ll_max_scale_factor).setOnClickListener(v -> showMaxScaleFactorDialog());
        view.findViewById(R.id.ll_author).setOnClickListener(v -> openAuthorProfile());
        view.findViewById(R.id.ll_github).setOnClickListener(v -> openGitHubRepository());

        updateUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            openProjectWebsite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ... existing methods ...

    private void updateUI() {
        int style = settingsManager.getArrowStyle();
        if (style == SettingsManager.STYLE_T_ARROW_T)
            tvArrowStyle.setText(R.string.style_t_arrow_t);
        else if (style == SettingsManager.STYLE_T_T)
            tvArrowStyle.setText(R.string.style_t_t);
        else if (style == SettingsManager.STYLE_ARROW_ARROW)
            tvArrowStyle.setText(R.string.style_arrow_arrow);

        String lang = settingsManager.getLanguage();
        if ("auto".equals(lang))
            tvLanguage.setText("Auto");
        else if ("en".equals(lang))
            tvLanguage.setText("English");
        else if ("zh".equals(lang))
            tvLanguage.setText("中文");

        float maxScaleFactor = settingsManager.getMaxScaleFactor();
        tvMaxScaleFactor.setText(String.format("%.1f", maxScaleFactor));

        // Get version info dynamically
        try {
            String versionName = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionName;
            int versionCode = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionCode;
            tvVersion.setText(versionName + " (" + versionCode + ")");
        } catch (Exception e) {
            tvVersion.setText("Unknown");
        }
    }

    private void showArrowStyleDialog() {
        String[] items = { getString(R.string.style_t_arrow_t), getString(R.string.style_t_t),
                getString(R.string.style_arrow_arrow) };
        int selected = settingsManager.getArrowStyle();
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_arrow_style_title)
                .setSingleChoiceItems(items, selected, (dialog, which) -> {
                    settingsManager.setArrowStyle(which);
                    updateUI();
                    dialog.dismiss();
                })
                .show();
    }

    private void showLanguageDialog() {
        String[] items = { "Auto", "English", "中文" };
        String[] values = { "auto", "en", "zh" };

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

    private void showMaxScaleFactorDialog() {
        // Create options for scale factor: 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0
        String[] items = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        float[] values = { 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f };

        // Find current selection
        float current = settingsManager.getMaxScaleFactor();
        int selected = 3; // default to 2.5
        for (int i = 0; i < values.length; i++) {
            if (Math.abs(values[i] - current) < 0.01f) {
                selected = i;
                break;
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_max_scale_factor)
                .setSingleChoiceItems(items, selected, (dialog, which) -> {
                    settingsManager.setMaxScaleFactor(values[which]);
                    updateUI();
                    dialog.dismiss();
                })
                .show();
    }

    private void openAuthorProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zfdang"));
        startActivity(intent);
    }

    private void openProjectWebsite() {
        String url = "https://dimcam.zfdang.com";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openGitHubRepository() {
        String url = "https://github.com/zfdang/dimension-marking-camera";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
