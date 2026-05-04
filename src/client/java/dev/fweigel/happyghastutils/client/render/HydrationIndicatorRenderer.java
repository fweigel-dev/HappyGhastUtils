package dev.fweigel.happyghastutils.client.render;

import dev.fweigel.happyghastutils.client.HappyGhastConfig;
import dev.fweigel.happyghastutils.client.HydrationTracker;
import dev.fweigel.mobutils.core.client.render.WorldTextRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DriedGhastBlock;

import java.util.Map;

public final class HydrationIndicatorRenderer {

    private static final int RENDER_DISTANCE = 16;

    public static void render(LevelRenderContext context) {
        if (!HappyGhastConfig.isHydrationIndicatorEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        int maxHydration = DriedGhastBlock.MAX_HYDRATION_LEVEL;

        for (Map.Entry<BlockPos, Integer> entry : HydrationTracker.getTrackedBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            if (playerPos.distSqr(pos) > RENDER_DISTANCE * RENDER_DISTANCE) continue;

            int hydration = entry.getValue();
            int color = hydration < 2 ? 0xFFFFAA00 : hydration < maxHydration ? 0xFFFFFF00 : 0xFF00FF00;

            WorldTextRenderer.renderFloatingText(context, hydration + "/" + maxHydration,
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, color);
        }
    }

    private HydrationIndicatorRenderer() {}
}
