package dev.fweigel.happyghastutils.network;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import dev.fweigel.mobutils.core.network.ModHandshake;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class HappyGhastPayloads {

    public static final ModHandshake HANDSHAKE = new ModHandshake(HappyGhastUtils.MOD_ID);

    public record BabyAgeSyncS2C(int entityId, int age) implements CustomPacketPayload {
        public static final Type<BabyAgeSyncS2C> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(HappyGhastUtils.MOD_ID, "baby_age_sync"));

        public static final StreamCodec<FriendlyByteBuf, BabyAgeSyncS2C> STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> {
                            buf.writeVarInt(value.entityId);
                            buf.writeInt(value.age);
                        },
                        buf -> new BabyAgeSyncS2C(buf.readVarInt(), buf.readInt())
                );

        @Override
        public Type<BabyAgeSyncS2C> type() {
            return TYPE;
        }
    }

    public static void registerAll() {
        HANDSHAKE.registerPayloads();
        PayloadTypeRegistry.clientboundPlay().register(BabyAgeSyncS2C.TYPE, BabyAgeSyncS2C.STREAM_CODEC);
    }

    private HappyGhastPayloads() {}
}
