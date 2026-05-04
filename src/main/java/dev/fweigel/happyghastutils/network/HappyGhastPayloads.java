package dev.fweigel.happyghastutils.network;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class HappyGhastPayloads {

    public record HelloC2S() implements CustomPacketPayload {
        public static final Type<HelloC2S> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(HappyGhastUtils.MOD_ID, "hello"));

        @Override
        public Type<HelloC2S> type() {
            return TYPE;
        }
    }

    public record HelloAckS2C() implements CustomPacketPayload {
        public static final Type<HelloAckS2C> TYPE = new Type<>(
                Identifier.fromNamespaceAndPath(HappyGhastUtils.MOD_ID, "hello_ack"));

        @Override
        public Type<HelloAckS2C> type() {
            return TYPE;
        }
    }

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
        PayloadTypeRegistry.serverboundPlay().register(HelloC2S.TYPE, StreamCodec.unit(new HelloC2S()));
        PayloadTypeRegistry.clientboundPlay().register(HelloAckS2C.TYPE, StreamCodec.unit(new HelloAckS2C()));
        PayloadTypeRegistry.clientboundPlay().register(BabyAgeSyncS2C.TYPE, BabyAgeSyncS2C.STREAM_CODEC);
    }

    private HappyGhastPayloads() {}
}
