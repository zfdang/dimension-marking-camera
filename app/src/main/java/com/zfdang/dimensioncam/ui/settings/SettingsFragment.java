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
        tvVersion = view.findViewById(R.id.tv_version_value);

        view.findViewById(R.id.ll_arrow_style).setOnClickListener(v -> showArrowStyleDialog());
        view.findViewById(R.id.ll_language).setOnClickListener(v -> showLanguageDialog());
        view.findViewById(R.id.ll_author).setOnClickListener(v -> openAuthorProfile());
        view.findViewById(R.id.ll_github).setOnClickListener(v -> openGitHubProject());

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
            openGitHubProject();
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

    private void openAuthorProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zfdang"));
        startActivity(intent);
    }

    private void openGitHubProject() {
        String url = "https://github.com/zfdang/dimension-marking-camera";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
