package dev.fweigel.happyghastutils.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class HydrationTracker {

    private static final Map<BlockPos, Integer> trackedBlocks = new HashMap<>();
    private static int tickCounter = 0;
    private static final int SCAN_INTERVAL = 20;
    private static final int SCAN_RADIUS = 16;

    public static void tick(Level level, Player player) {
        tickCounter++;
        if (tickCounter < SCAN_INTERVAL) {
            return;
        }
        tickCounter = 0;

        BlockPos playerPos = player.blockPosition();
        Map<BlockPos, Integer> found = new HashMap<>();

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; dy++) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.DRIED_GHAST)) {
                        int hydration = state.getValue(DriedGhastBlock.HYDRATION_LEVEL);
                        found.put(pos.immutable(), hydration);
                    }
                }
            }
        }

        trackedBlocks.clear();
        trackedBlocks.putAll(found);
    }

    public static Map<BlockPos, Integer> getTrackedBlocks() {
        return trackedBlocks;
    }

    public static void clear() {
        trackedBlocks.clear();
        tickCounter = 0;
    }

    private HydrationTracker() {}
}
