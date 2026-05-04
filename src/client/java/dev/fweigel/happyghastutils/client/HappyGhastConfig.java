package dev.fweigel.happyghastutils.client;

public class HappyGhastConfig {
    private static float ghastVolume = 1.0f;
    private static float ghastlingVolume = 1.0f;
    private static boolean hydrationIndicatorEnabled = true;
    private static boolean babyTimerEnabled = true;
    private static boolean navigationHudEnabled = true;

    public static float getGhastVolume() {
        return ghastVolume;
    }

    public static void setGhastVolume(float v) {
        ghastVolume = Math.max(0.0f, Math.min(1.0f, v));
    }

    public static float getGhastlingVolume() {
        return ghastlingVolume;
    }

    public static void setGhastlingVolume(float v) {
        ghastlingVolume = Math.max(0.0f, Math.min(1.0f, v));
    }

    public static boolean isHydrationIndicatorEnabled() {
        return hydrationIndicatorEnabled;
    }

    public static void setHydrationIndicatorEnabled(boolean enabled) {
        hydrationIndicatorEnabled = enabled;
    }

    public static void toggleHydrationIndicator() {
        hydrationIndicatorEnabled = !hydrationIndicatorEnabled;
    }

    public static boolean isBabyTimerEnabled() {
        return babyTimerEnabled;
    }

    public static void setBabyTimerEnabled(boolean enabled) {
        babyTimerEnabled = enabled;
    }

    public static void toggleBabyTimer() {
        babyTimerEnabled = !babyTimerEnabled;
    }

    public static boolean isNavigationHudEnabled() {
        return navigationHudEnabled;
    }

    public static void setNavigationHudEnabled(boolean enabled) {
        navigationHudEnabled = enabled;
    }

    public static void toggleNavigationHud() {
        navigationHudEnabled = !navigationHudEnabled;
    }

    public static void reset() {
        ghastVolume = 1.0f;
        ghastlingVolume = 1.0f;
        hydrationIndicatorEnabled = true;
        babyTimerEnabled = true;
        navigationHudEnabled = true;
    }
}
