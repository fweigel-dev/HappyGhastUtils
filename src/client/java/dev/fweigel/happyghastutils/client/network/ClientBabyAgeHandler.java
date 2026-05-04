package dev.fweigel.happyghastutils.client.network;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import dev.fweigel.happyghastutils.network.HappyGhastPayloads;
import dev.fweigel.mobutils.core.client.network.ClientHandshakeTracker;
import dev.fweigel.mobutils.core.client.render.BabyAgeTracker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientBabyAgeHandler {

    private static final ClientHandshakeTracker handshake = new ClientHandshakeTracker();

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(HappyGhastPayloads.HelloAckS2C.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                handshake.onAck();
                HappyGhastUtils.LOGGER.info("Baby timer: server has mod");
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(HappyGhastPayloads.BabyAgeSyncS2C.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                BabyAgeTracker.update(payload.entityId(), payload.age());
            });
        });
    }

    public static void onJoin() {
        if (ClientPlayNetworking.canSend(HappyGhastPayloads.HelloC2S.TYPE)) {
            ClientPlayNetworking.send(new HappyGhastPayloads.HelloC2S());
            handshake.startHandshake();
        } else {
            HappyGhastUtils.LOGGER.info("Baby timer: server does not support handshake");
        }
    }

    public static void onDisconnect() {
        handshake.reset();
        BabyAgeTracker.clear();
    }

    public static void tick() {
        if (handshake.tick()) {
            HappyGhastUtils.LOGGER.info("Baby timer: handshake timeout");
        }
    }

    public static boolean isServerHasMod() {
        return handshake.isServerHasMod();
    }

    private ClientBabyAgeHandler() {}
}
