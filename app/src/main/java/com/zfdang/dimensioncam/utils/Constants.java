package com.zfdang.dimensioncam.utils;

/**
 * Application-wide constants for rendering and interaction
 */
public class Constants {

    // Export image scaling
    // Standard screen width used as reference for scaling text and lines
    public static final float STANDARD_SCREEN_WIDTH = 1080f;

    // Touch interaction
    // Touch threshold in pixels for selecting control points
    public static final float TOUCH_THRESHOLD_DP = 50f;

    // Annotation rendering
    // Base text size for annotation labels (optimized for square root scaling)
    public static final float BASE_TEXT_SIZE = 48f;

    // Shadow layer radius for text readability
    public static final float SHADOW_RADIUS = 2f;

    // Control point circle radius
    public static final float CONTROL_POINT_RADIUS = 20f;

    // Minimum arrow/endpoint size
    public static final float MIN_ARROW_SIZE = 30f;

    private Constants() {
        // Prevent instantiation
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
