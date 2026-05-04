package dev.fweigel.happyghastutils;

import dev.fweigel.happyghastutils.network.HappyGhastPayloads;
import dev.fweigel.happyghastutils.network.ServerBabyAgeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HappyGhastUtils implements ModInitializer {
    public static final String MOD_ID = "happyghastutils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HappyGhastPayloads.registerAll();
        ServerBabyAgeHandler.register();
        ServerTickEvents.END_SERVER_TICK.register(ServerBabyAgeHandler::tick);
        LOGGER.info("Happy Ghast Utils initialized");
    }
}
