package net.ogbonex.pollithium;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean dragging;

    public HudEditorScreen() {
        super(Text.literal("Pollithium HUD Editor"));
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 100;
        int top = this.height / 2 - 70;
        addDrawableChild(ButtonWidget.builder(toggle("FPS", PollithiumConfig.showFps),
                button -> { PollithiumConfig.showFps = !PollithiumConfig.showFps; PollithiumConfig.save(); refresh(); })
                .dimensions(left, top, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(toggle("Coordinates", PollithiumConfig.showCoordinates),
                button -> { PollithiumConfig.showCoordinates = !PollithiumConfig.showCoordinates; PollithiumConfig.save(); refresh(); })
                .dimensions(left, top + 25, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(toggle("Ping", PollithiumConfig.showPing),
                button -> { PollithiumConfig.showPing = !PollithiumConfig.showPing; PollithiumConfig.save(); refresh(); })
                .dimensions(left, top + 50, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(toggle("Keystrokes", PollithiumConfig.showKeystrokes),
                button -> { PollithiumConfig.showKeystrokes = !PollithiumConfig.showKeystrokes; PollithiumConfig.save(); refresh(); })
                .dimensions(left, top + 75, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset HUD position"),
                button -> { PollithiumConfig.hudX = 8; PollithiumConfig.hudY = 8; PollithiumConfig.save(); })
                .dimensions(left, top + 100, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions(left, top + 125, 200, 20).build());
    }

    private Text toggle(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    private void refresh() {
        clearChildren();
        init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 100, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                "Drag the HUD preview to reposition it", width / 2, height / 2 + 95, 0xFFCCCCCC);
        int x = PollithiumConfig.hudX;
        int y = PollithiumConfig.hudY;
        context.fill(x, y, x + 126, y + 32, 0xCC0D111B);
        context.fill(x, y, x + 126, y + 2, 0xFF8B5CF6);
        context.drawText(textRenderer, "Pollithium HUD preview", x + 7, y + 10, 0xFFFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= PollithiumConfig.hudX && mouseX <= PollithiumConfig.hudX + 126
                && mouseY >= PollithiumConfig.hudY && mouseY <= PollithiumConfig.hudY + 32) {
            dragging = true;
            dragOffsetX = (int) mouseX - PollithiumConfig.hudX;
            dragOffsetY = (int) mouseY - PollithiumConfig.hudY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            PollithiumConfig.hudX = Math.max(0, Math.min(width - 126, (int) mouseX - dragOffsetX));
            PollithiumConfig.hudY = Math.max(0, Math.min(height - 32, (int) mouseY - dragOffsetY));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            PollithiumConfig.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        PollithiumConfig.save();
        if (client != null) client.setScreen(null);
    }
}
