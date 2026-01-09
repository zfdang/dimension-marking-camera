package com.zfdang.dimensioncam.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.zfdang.dimensioncam.R;
import com.zfdang.dimensioncam.ui.annotation.AnnotationFragment;
import com.zfdang.dimensioncam.ui.photos.PhotosFragment;
import com.zfdang.dimensioncam.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private Fragment photosFragment;
    private Fragment annotationFragment;
    private Fragment settingsFragment;
    private Fragment activeFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);

        navView = findViewById(R.id.nav_view);

        View root = findViewById(R.id.container);
        applyEdgeToEdgeInsets(root, toolbar, navView);
        updateSystemBarAppearance(root);

        // Initialize fragments
        // Check if fragments already exist (e.g. after rotation)
        FragmentManager fm = getSupportFragmentManager();
        photosFragment = fm.findFragmentByTag("1");
        annotationFragment = fm.findFragmentByTag("2");
        settingsFragment = fm.findFragmentByTag("3");

        if (photosFragment == null) {
            photosFragment = new PhotosFragment();
            annotationFragment = new AnnotationFragment();
            settingsFragment = new SettingsFragment();

            fm.beginTransaction().add(R.id.nav_host_fragment, settingsFragment, "3").hide(settingsFragment).commit();
            fm.beginTransaction().add(R.id.nav_host_fragment, annotationFragment, "2").hide(annotationFragment)
                    .commit();
            fm.beginTransaction().add(R.id.nav_host_fragment, photosFragment, "1").commit();
            activeFragment = photosFragment;
        } else {
            // Restore active fragment state if needed, or just default to photos
            // For simplicity, finding the visible one
            if (!photosFragment.isHidden())
                activeFragment = photosFragment;
            else if (!annotationFragment.isHidden())
                activeFragment = annotationFragment;
            else if (!settingsFragment.isHidden())
                activeFragment = settingsFragment;
        }

        // Set initial title
        updateActionBarTitle();

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_photos) {
                    fm.beginTransaction().hide(activeFragment).show(photosFragment).commit();
                    activeFragment = photosFragment;
                    updateActionBarTitle();
                    invalidateOptionsMenu();
                    return true;
                } else if (itemId == R.id.navigation_annotation) {
                    fm.beginTransaction().hide(activeFragment).show(annotationFragment).commit();
                    activeFragment = annotationFragment;
                    updateActionBarTitle();
                    invalidateOptionsMenu();
                    return true;
                } else if (itemId == R.id.navigation_settings) {
                    fm.beginTransaction().hide(activeFragment).show(settingsFragment).commit();
                    activeFragment = settingsFragment;
                    updateActionBarTitle();
                    invalidateOptionsMenu();
                    return true;
                }
                return false;
            }
        });
    }

    private void applyEdgeToEdgeInsets(@NonNull View root, @NonNull View toolbar,
            @NonNull BottomNavigationView bottomNav) {
        final int toolbarPaddingLeft = toolbar.getPaddingLeft();
        final int toolbarPaddingTop = toolbar.getPaddingTop();
        final int toolbarPaddingRight = toolbar.getPaddingRight();
        final int toolbarPaddingBottom = toolbar.getPaddingBottom();

        final int bottomNavPaddingLeft = bottomNav.getPaddingLeft();
        final int bottomNavPaddingTop = bottomNav.getPaddingTop();
        final int bottomNavPaddingRight = bottomNav.getPaddingRight();
        final int bottomNavPaddingBottom = bottomNav.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());

            toolbar.setPadding(
                    toolbarPaddingLeft + systemBars.left,
                    toolbarPaddingTop + systemBars.top,
                    toolbarPaddingRight + systemBars.right,
                    toolbarPaddingBottom);

            bottomNav.setPadding(
                    bottomNavPaddingLeft + systemBars.left,
                    bottomNavPaddingTop,
                    bottomNavPaddingRight + systemBars.right,
                    bottomNavPaddingBottom + systemBars.bottom);

            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void updateSystemBarAppearance(@NonNull View root) {
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), root);

        int statusBarBg = resolveThemeColor(this, com.google.android.material.R.attr.colorPrimary);
        int navBarBg = resolveThemeColor(this, com.google.android.material.R.attr.colorSurface);

        controller.setAppearanceLightStatusBars(isLightColor(statusBarBg));
        controller.setAppearanceLightNavigationBars(isLightColor(navBarBg));
    }

    private static boolean isLightColor(int color) {
        return ColorUtils.calculateLuminance(color) > 0.5;
    }

    private static int resolveThemeColor(@NonNull Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(attrResId, typedValue, true)) {
            return Color.BLACK;
        }
        if (typedValue.resourceId != 0) {
            return ContextCompat.getColor(context, typedValue.resourceId);
        }
        return typedValue.data;
    }

    private void updateActionBarTitle() {
        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            if (activeFragment == photosFragment) {
                getSupportActionBar().setTitle(appName + " - " + getString(R.string.title_photos));
            } else if (activeFragment == annotationFragment) {
                getSupportActionBar().setTitle(appName + " - " + getString(R.string.title_annotate));
            } else if (activeFragment == settingsFragment) {
                getSupportActionBar().setTitle(appName + " - " + getString(R.string.title_settings));
            }
        }
    }

    public void navigateToAnnotation(long photoId) {
        // Switch to Annotation tab and pass photoId
        navView.setSelectedItemId(R.id.navigation_annotation);
        if (annotationFragment instanceof AnnotationFragment) {
            ((AnnotationFragment) annotationFragment).loadPhoto(photoId);
        }
    }

    public void restart() {
        recreate();
    }
}
