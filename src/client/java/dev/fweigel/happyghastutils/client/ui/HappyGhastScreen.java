package dev.fweigel.happyghastutils.client.ui;

import dev.fweigel.happyghastutils.client.HappyGhastConfig;
import dev.fweigel.happyghastutils.client.HappyGhastStorage;
import dev.fweigel.happyghastutils.client.network.ClientBabyAgeHandler;
import dev.fweigel.mobutils.core.client.ui.ModOptionsList;
import dev.fweigel.mobutils.core.client.ui.ModOptionsList.CardSpec;
import dev.fweigel.mobutils.core.client.ui.ModSettingsScreen;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;

public class HappyGhastScreen extends ModSettingsScreen {

    private static final Identifier IMG_HYDRATION_ON  = id("hydration_on.png");
    private static final Identifier IMG_HYDRATION_OFF = id("hydration_off.png");
    private static final Identifier IMG_BABY_TIMER_ON  = id("baby_timer_on.png");
    private static final Identifier IMG_BABY_TIMER_OFF = id("baby_timer_off.png");

    public HappyGhastScreen() {
        super(Component.translatable("happyghastutils.screen.title"));
    }

    @Override
    protected void addOptions(ModOptionsList list) {
        Button babyTimerBtn = buildHalfButton(this::getBabyTimerLabel, () -> {
            HappyGhastConfig.toggleBabyTimer();
            HappyGhastStorage.save();
        });
        babyTimerBtn.active = ClientBabyAgeHandler.isServerHasMod();

        list.addSplitCard(
            CardSpec.image(() -> HappyGhastConfig.isHydrationIndicatorEnabled() ? IMG_HYDRATION_ON : IMG_HYDRATION_OFF),
            buildHalfButton(this::getHydrationLabel, () -> {
                HappyGhastConfig.toggleHydrationIndicator();
                HappyGhastStorage.save();
            }),
            CardSpec.image(() -> HappyGhastConfig.isBabyTimerEnabled() ? IMG_BABY_TIMER_ON : IMG_BABY_TIMER_OFF),
            babyTimerBtn
        );

        list.addWide(buildWideButton(this::getNavigationHudLabel, () -> {
            HappyGhastConfig.toggleNavigationHud();
            HappyGhastStorage.save();
        }));

        boolean ridingGhast = this.minecraft.player != null
                && this.minecraft.player.getVehicle() instanceof HappyGhast;
        Button navButton = Button.builder(
                Component.translatable("happyghastutils.screen.navigation"),
                b -> this.minecraft.setScreenAndShow(new NavigationScreen())
        ).bounds(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        navButton.active = ridingGhast;
        if (!ridingGhast) {
            navButton.setTooltip(Tooltip.create(
                    Component.translatable("happyghastutils.screen.navigation.tooltip")));
        }
        list.addWide(navButton);

        list.addWide(new AbstractSliderButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT,
                getVolumeLabel(HappyGhastConfig.getGhastVolume()), HappyGhastConfig.getGhastVolume()) {
            @Override
            protected void updateMessage() { setMessage(getVolumeLabel((float) this.value)); }
            @Override
            protected void applyValue() {
                HappyGhastConfig.setGhastVolume((float) this.value);
                HappyGhastStorage.save();
            }
        });

        list.addWide(new AbstractSliderButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT,
                getGhastlingVolumeLabel(HappyGhastConfig.getGhastlingVolume()), HappyGhastConfig.getGhastlingVolume()) {
            @Override
            protected void updateMessage() { setMessage(getGhastlingVolumeLabel((float) this.value)); }
            @Override
            protected void applyValue() {
                HappyGhastConfig.setGhastlingVolume((float) this.value);
                HappyGhastStorage.save();
            }
        });
    }

    private static Identifier id(String path) {
        return Identifier.parse("happyghastutils:textures/gui/preview/" + path);
    }

    private String stateText(boolean on) {
        return Component.translatable(on ? "happyghastutils.state.on" : "happyghastutils.state.off").getString();
    }

    private Component getHydrationLabel() {
        return Component.translatable("happyghastutils.screen.hydration_indicator.card",
                stateText(HappyGhastConfig.isHydrationIndicatorEnabled()));
    }

    private Component getBabyTimerLabel() {
        if (!ClientBabyAgeHandler.isServerHasMod()) {
            return Component.translatable("happyghastutils.screen.baby_timer.unavailable");
        }
        return Component.translatable("happyghastutils.screen.baby_timer.card",
                stateText(HappyGhastConfig.isBabyTimerEnabled()));
    }

    private Component getNavigationHudLabel() {
        return Component.translatable("happyghastutils.screen.navigation_hud",
                stateText(HappyGhastConfig.isNavigationHudEnabled()));
    }

    private Component getVolumeLabel(float volume) {
        return Component.translatable("happyghastutils.screen.sound_volume",
                Math.round(volume * 100) + "%");
    }

    private Component getGhastlingVolumeLabel(float volume) {
        return Component.translatable("happyghastutils.screen.sound_volume_ghastling",
                Math.round(volume * 100) + "%");
    }
}
