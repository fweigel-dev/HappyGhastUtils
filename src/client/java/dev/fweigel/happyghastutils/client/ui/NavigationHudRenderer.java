package dev.fweigel.happyghastutils.client.ui;

import dev.fweigel.happyghastutils.client.GhastAutopilot;
import dev.fweigel.happyghastutils.client.HappyGhastConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public final class NavigationHudRenderer {

    private NavigationHudRenderer() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        // Note: in 26.2 the GUI-hidden (F1) flag is no longer publicly accessible; Fabric's
        // HudElementRegistry elements are part of the vanilla layered HUD, which is already
        // skipped while the GUI is hidden, so no explicit check is needed here.
        if (minecraft.player == null || !HappyGhastConfig.isNavigationHudEnabled()) return;

        if (GhastAutopilot.isActive()) {
            int y = 5;
            int x = 5;

            Component targetText = Component.translatable("happyghastutils.nav.hud.target",
                    (int) GhastAutopilot.getTargetX(),
                    (int) GhastAutopilot.getTargetY(),
                    (int) GhastAutopilot.getTargetZ());
            graphics.text(minecraft.font, targetText, x, y, 0xFFFFFFFF, true);
            y += 12;

            Entity vehicle = minecraft.player.getVehicle();
            if (vehicle != null) {
                int totalDist = (int) GhastAutopilot.getTotalDistance();
                int traveled = Math.max(0, (int) GhastAutopilot.getTraveledDistance(vehicle));
                int remaining = (int) GhastAutopilot.getRemainingDistance(vehicle);

                graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.total_distance", totalDist), x, y, 0xFFFFFFFF, true);
                y += 12;
                graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.traveled", traveled, totalDist), x, y, 0xFF55FF55, true);
                y += 12;
                graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.remaining", remaining), x, y, 0xFFFFFFFF, true);
                y += 12;

                int etaSeconds = GhastAutopilot.getEstimatedSecondsRemaining(vehicle);
                if (etaSeconds >= 0) {
                    graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.eta", formatTime(etaSeconds)), x, y, 0xFFFFAA00, true);
                    y += 12;
                }
            }

            graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.cancel_hint"), x, y, 0xFFAAAAAA, true);

        } else if (GhastAutopilot.isArrivedRecently()) {
            graphics.text(minecraft.font, Component.translatable("happyghastutils.nav.hud.arrived"), 5, 5, 0xFF55FF55, true);
        }
    }

    private static String formatTime(int totalSeconds) {
        if (totalSeconds < 60) return totalSeconds + "s";
        return (totalSeconds / 60) + "m " + (totalSeconds % 60) + "s";
    }
}
