package dev.fweigel.happyghastutils.client.network;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import dev.fweigel.happyghastutils.network.HappyGhastPayloads;
import dev.fweigel.mobutils.core.client.network.ModHandshakeClient;
import dev.fweigel.mobutils.core.client.render.BabyAgeTracker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientBabyAgeHandler {

    private static final ModHandshakeClient handshakeClient =
            new ModHandshakeClient(HappyGhastPayloads.HANDSHAKE);

    public static void register() {
        handshakeClient.registerReceiver(() ->
                HappyGhastUtils.LOGGER.info("Baby timer: server has mod"));

        ClientPlayNetworking.registerGlobalReceiver(HappyGhastPayloads.BabyAgeSyncS2C.TYPE, (payload, context) -> {
            context.client().execute(() ->
                    BabyAgeTracker.update(payload.entityId(), payload.age()));
        });
    }

    public static void onJoin() {
        handshakeClient.onJoin(() ->
                HappyGhastUtils.LOGGER.info("Baby timer: server does not support handshake"));
    }

    public static void onDisconnect() {
        handshakeClient.onDisconnect();
        BabyAgeTracker.clear();
    }

    public static void tick() {
        if (handshakeClient.tick()) {
            HappyGhastUtils.LOGGER.info("Baby timer: handshake timeout");
        }
    }

    public static boolean isServerHasMod() {
        return handshakeClient.isServerHasMod();
    }

    private ClientBabyAgeHandler() {}
}
