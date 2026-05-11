package dev.fweigel.happyghastutils.network;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.phys.AABB;

import java.util.List;

public final class ServerBabyAgeHandler {

    private static int tickCounter = 0;
    private static final int SYNC_INTERVAL = 20;
    private static final double SCAN_RADIUS = 64.0;

    public static void register() {
        HappyGhastPayloads.HANDSHAKE.registerServerReceiver(HappyGhastUtils.LOGGER);
    }

    public static void tick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < SYNC_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayer player : HappyGhastPayloads.HANDSHAKE.getModPlayers().getAll()) {
            if (player.isRemoved()) continue;

            AABB area = player.getBoundingBox().inflate(SCAN_RADIUS);
            ServerLevel level = (ServerLevel) player.level();
            List<AgeableMob> babies = level.getEntitiesOfClass(
                    AgeableMob.class, area, AgeableMob::isBaby);

            for (AgeableMob mob : babies) {
                ServerPlayNetworking.send(player,
                        new HappyGhastPayloads.BabyAgeSyncS2C(mob.getId(), mob.getAge()));
            }
        }
    }

    private ServerBabyAgeHandler() {}
}
