package dev.fweigel.happyghastutils.client.ui;

import dev.fweigel.happyghastutils.client.GhastAutopilot;
import dev.fweigel.mobutils.core.client.ui.ModScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;

public class NavigationScreen extends ModScreen {

    private static final int FIELD_WIDTH = 80;
    private static final int FIELD_HEIGHT = 20;

    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private Button navigateButton;
    private Button cancelButton;

    public NavigationScreen() {
        super(Component.translatable("happyghastutils.nav.title"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 50;

        Minecraft mc = Minecraft.getInstance();
        String defaultX = mc.player != null ? String.valueOf(Math.round(mc.player.getX())) : "0";
        String defaultY = mc.player != null ? String.valueOf(Math.round(mc.player.getY())) : "0";
        String defaultZ = mc.player != null ? String.valueOf(Math.round(mc.player.getZ())) : "0";

        int totalFieldWidth = FIELD_WIDTH * 3 + 10 * 2;
        int startX = cx - totalFieldWidth / 2;

        xField = new EditBox(this.font, startX, y, FIELD_WIDTH, FIELD_HEIGHT, Component.literal("X"));
        xField.setValue(defaultX);
        xField.setHint(Component.literal("X"));
        addRenderableWidget(xField);

        yField = new EditBox(this.font, startX + FIELD_WIDTH + 10, y, FIELD_WIDTH, FIELD_HEIGHT, Component.literal("Y"));
        yField.setValue(defaultY);
        yField.setHint(Component.literal("Y"));
        addRenderableWidget(yField);

        zField = new EditBox(this.font, startX + (FIELD_WIDTH + 10) * 2, y, FIELD_WIDTH, FIELD_HEIGHT, Component.literal("Z"));
        zField.setValue(defaultZ);
        zField.setHint(Component.literal("Z"));
        addRenderableWidget(zField);

        y += 30;

        navigateButton = addRenderableWidget(Button.builder(
                Component.translatable("happyghastutils.nav.button.navigate"),
                button -> {
                    try {
                        int x1 = Integer.parseInt(xField.getValue());
                        int y1 = Integer.parseInt(yField.getValue());
                        int z1 = Integer.parseInt(zField.getValue());
                        GhastAutopilot.start(x1, y1, z1, Minecraft.getInstance().player.getVehicle());
                        this.onClose();
                    } catch (NumberFormatException ignored) {}
                }
        ).bounds(cx - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        y += 24;

        cancelButton = addRenderableWidget(Button.builder(
                Component.translatable("happyghastutils.nav.button.cancel"),
                button -> {
                    GhastAutopilot.cancel();
                    updateButtonStates();
                }
        ).bounds(cx - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        y += 24;

        addRenderableWidget(Button.builder(
                Component.translatable("happyghastutils.nav.button.back"),
                button -> Minecraft.getInstance().setScreen(new HappyGhastScreen())
        ).bounds(cx - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        updateButtonStates();
    }

    @Override
    public void tick() {
        super.tick();
        updateButtonStates();
    }

    private void updateButtonStates() {
        Minecraft mc = Minecraft.getInstance();
        boolean ridingGhast = mc.player != null && mc.player.getVehicle() instanceof HappyGhast;
        boolean validInput = isValidInteger(xField.getValue())
                && isValidInteger(yField.getValue())
                && isValidInteger(zField.getValue());
        navigateButton.active = ridingGhast && validInput && !GhastAutopilot.isActive();
        cancelButton.active = GhastAutopilot.isActive();
        cancelButton.visible = GhastAutopilot.isActive();
    }

    private boolean isValidInteger(String s) {
        if (s == null || s.isEmpty() || s.equals("-")) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int cx = this.width / 2;
        graphics.centeredText(this.font, this.title, cx, 15, 0xFFFFFFFF);
        int startX = cx - (FIELD_WIDTH * 3 + 10 * 2) / 2;
        int labelY = 39;
        graphics.text(this.font, "X", startX, labelY, 0xFFAAAAAA, false);
        graphics.text(this.font, "Y", startX + FIELD_WIDTH + 10, labelY, 0xFFAAAAAA, false);
        graphics.text(this.font, "Z", startX + (FIELD_WIDTH + 10) * 2, labelY, 0xFFAAAAAA, false);
    }
}
