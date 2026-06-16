package dev.fweigel.happyghastutils.client;

import dev.fweigel.happyghastutils.HappyGhastUtils;
import dev.fweigel.happyghastutils.client.network.ClientBabyAgeHandler;
import dev.fweigel.happyghastutils.client.render.HydrationIndicatorRenderer;
import dev.fweigel.happyghastutils.client.ui.HappyGhastScreen;
import dev.fweigel.happyghastutils.client.ui.NavigationHudRenderer;
import dev.fweigel.mobutils.core.client.render.BabyTimerRenderer;
import dev.fweigel.mobutils.core.client.sound.SoundVolumeRegistry;
import dev.fweigel.mobutils.core.client.util.ConfigKeyHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.lwjgl.glfw.GLFW;

public class HappyGhastUtilsClient implements ClientModInitializer {
    private static KeyMapping configKey;

    @Override
    public void onInitializeClient() {
        configKey = ConfigKeyHelper.register(HappyGhastUtils.MOD_ID, "key.happyghastutils.config", GLFW.GLFW_KEY_H);

        SoundVolumeRegistry.register("entity.happy_ghast.", HappyGhastConfig::getGhastVolume);
        SoundVolumeRegistry.register("entity.ghastling.", HappyGhastConfig::getGhastlingVolume);

        ClientBabyAgeHandler.register();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            HappyGhastStorage.loadForWorld(client);
            ClientBabyAgeHandler.onJoin();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            HydrationTracker.clear();
            GhastAutopilot.cancel();
            ClientBabyAgeHandler.onDisconnect();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKey.consumeClick()) {
                client.setScreenAndShow(new HappyGhastScreen());
            }
            GhastAutopilot.tick(client);
            if (client.level != null && client.player != null) {
                HydrationTracker.tick(client.level, client.player);
            }
            ClientBabyAgeHandler.tick();
        });

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(HappyGhastUtils.MOD_ID, "navigation_hud"),
                NavigationHudRenderer::render
        );

        LevelRenderEvents.COLLECT_SUBMITS.register(HydrationIndicatorRenderer::render);

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            if (HappyGhastConfig.isBabyTimerEnabled() && ClientBabyAgeHandler.isServerHasMod()) {
                BabyTimerRenderer.render(context, e -> e instanceof HappyGhast);
            }
        });
    }
}
