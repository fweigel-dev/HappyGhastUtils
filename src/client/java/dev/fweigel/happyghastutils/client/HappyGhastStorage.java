package dev.fweigel.happyghastutils.client;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import dev.fweigel.mobutils.core.client.storage.WorldScopedStorage;
import net.minecraft.client.Minecraft;

public class HappyGhastStorage {

    private static final WorldScopedStorage<SaveData> STORAGE =
            new WorldScopedStorage<>("happyghastutils", SaveData.class, HappyGhastUtils.LOGGER);

    private static class SaveData {
        Float ghastVolume;
        Float ghastlingVolume;
        Boolean hydrationIndicatorEnabled;
        Boolean babyTimerEnabled;
        Boolean navigationHudEnabled;
    }

    public static void loadForWorld(Minecraft client) {
        STORAGE.loadForWorld(client).ifPresentOrElse(data -> {
            HappyGhastConfig.setGhastVolume(data.ghastVolume != null ? data.ghastVolume : 1.0f);
            HappyGhastConfig.setGhastlingVolume(data.ghastlingVolume != null ? data.ghastlingVolume : 1.0f);
            HappyGhastConfig.setHydrationIndicatorEnabled(data.hydrationIndicatorEnabled == null || data.hydrationIndicatorEnabled);
            HappyGhastConfig.setBabyTimerEnabled(data.babyTimerEnabled == null || data.babyTimerEnabled);
            HappyGhastConfig.setNavigationHudEnabled(data.navigationHudEnabled == null || data.navigationHudEnabled);
        }, HappyGhastConfig::reset);
    }

    public static void save() {
        SaveData data = new SaveData();
        data.ghastVolume = HappyGhastConfig.getGhastVolume();
        data.ghastlingVolume = HappyGhastConfig.getGhastlingVolume();
        data.hydrationIndicatorEnabled = HappyGhastConfig.isHydrationIndicatorEnabled();
        data.babyTimerEnabled = HappyGhastConfig.isBabyTimerEnabled();
        data.navigationHudEnabled = HappyGhastConfig.isNavigationHudEnabled();
        STORAGE.save(data);
    }
}
