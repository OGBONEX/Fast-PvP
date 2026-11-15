package net.ogbonex.pollithium;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public final class HudEditorScreen extends Screen {
    private static final Map<String, String> WIDGETS = new LinkedHashMap<>();
    static {
        WIDGETS.put("FPS", "FPS");
        WIDGETS.put("PING", "Ping");
        WIDGETS.put("COORDS", "Coordinates");
        WIDGETS.put("DIRECTION", "Direction");
        WIDGETS.put("CLOCK", "Clock");
        WIDGETS.put("CPS", "CPS");
        WIDGETS.put("ARMOR", "Armor");
        WIDGETS.put("EFFECTS", "Potion effects");
        WIDGETS.put("KEYS", "Keystrokes");
    }

    private String selected = "FPS";
    private boolean dragging;
    private boolean resizing;
    private double lastMouseX, lastMouseY;
    private TextFieldWidget colorField;

    public HudEditorScreen() { super(Text.literal("Pollithium HUD Editor")); }

    @Override
    protected void init() {
        int left = 8, top = 26;
        addToggle("FPS", PollithiumConfig.showFps, () -> PollithiumConfig.showFps = !PollithiumConfig.showFps, () -> PollithiumConfig.showFps, left, top);
        addToggle("Ping", PollithiumConfig.showPing, () -> PollithiumConfig.showPing = !PollithiumConfig.showPing, () -> PollithiumConfig.showPing, left, top + 23);
        addToggle("Coordinates", PollithiumConfig.showCoordinates, () -> PollithiumConfig.showCoordinates = !PollithiumConfig.showCoordinates, () -> PollithiumConfig.showCoordinates, left, top + 46);
        addToggle("Keystrokes", PollithiumConfig.showKeystrokes, () -> PollithiumConfig.showKeystrokes = !PollithiumConfig.showKeystrokes, () -> PollithiumConfig.showKeystrokes, left, top + 69);
        addToggle("CPS", PollithiumConfig.showCps, () -> PollithiumConfig.showCps = !PollithiumConfig.showCps, () -> PollithiumConfig.showCps, left, top + 92);
        addToggle("Armor", PollithiumConfig.showArmor, () -> PollithiumConfig.showArmor = !PollithiumConfig.showArmor, () -> PollithiumConfig.showArmor, left, top + 115);
        addToggle("Potion effects", PollithiumConfig.showEffects, () -> PollithiumConfig.showEffects = !PollithiumConfig.showEffects, () -> PollithiumConfig.showEffects, left, top + 138);
        addToggle("Clock", PollithiumConfig.showClock, () -> PollithiumConfig.showClock = !PollithiumConfig.showClock, () -> PollithiumConfig.showClock, left, top + 161);
        addToggle("Direction", PollithiumConfig.showDirection, () -> PollithiumConfig.showDirection = !PollithiumConfig.showDirection, () -> PollithiumConfig.showDirection, left, top + 184);
        addToggle("Low fire", PollithiumConfig.lowFireEnabled, () -> PollithiumConfig.lowFireEnabled = !PollithiumConfig.lowFireEnabled, () -> PollithiumConfig.lowFireEnabled, left, top + 207);
        addToggle("Shulker tooltip", PollithiumConfig.shulkerBoxTooltipEnabled, () -> PollithiumConfig.shulkerBoxTooltipEnabled = !PollithiumConfig.shulkerBoxTooltipEnabled, () -> PollithiumConfig.shulkerBoxTooltipEnabled, left, top + 230);
        addToggle("Custom crosshair", PollithiumConfig.showCrosshair, () -> PollithiumConfig.showCrosshair = !PollithiumConfig.showCrosshair, () -> PollithiumConfig.showCrosshair, left, top + 253);
        addToggle("Toggle sprint", PollithiumConfig.toggleSprintEnabled, () -> PollithiumConfig.toggleSprintEnabled = !PollithiumConfig.toggleSprintEnabled, () -> PollithiumConfig.toggleSprintEnabled, left, top + 276);

        addDrawableChild(ButtonWidget.builder(Text.literal("Selected: " + WIDGETS.get(selected)), b -> {
            String[] ids = WIDGETS.keySet().toArray(String[]::new);
            int index = java.util.Arrays.asList(ids).indexOf(selected);
            selected = ids[(index + 1) % ids.length];
            b.setMessage(Text.literal("Selected: " + WIDGETS.get(selected)));
        }).dimensions(224, 26, 190, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset selected widget"), b -> {
            PollithiumConfig.widgets.remove(selected);
            PollithiumConfig.save();
        }).dimensions(224, 50, 190, 20).build());

        colorField = new TextFieldWidget(textRenderer, 224, 88, 110, 20, Text.literal("Accent HEX"));
        colorField.setMaxLength(8);
        colorField.setText(String.format("%08X", PollithiumConfig.accentColor));
        colorField.setPlaceholder(Text.literal("AARRGGBB"));
        addDrawableChild(colorField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Apply color"), b -> applyColor())
                .dimensions(338, 88, 76, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset HUD positions"), b -> {
            PollithiumConfig.widgets.clear();
            PollithiumConfig.hudX = 8; PollithiumConfig.hudY = 8;
            PollithiumConfig.save();
        }).dimensions(224, 114, 190, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(224, 138, 190, 20).build());
    }

    private void addToggle(String name, boolean initial, Runnable change, BooleanSupplier state, int x, int y) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (initial ? "ON" : "OFF")), b -> {
            change.run();
            b.setMessage(Text.literal(name + ": " + (state.getAsBoolean() ? "ON" : "OFF")));
            PollithiumConfig.save();
        }).dimensions(x, y, 200, 20).build());
    }

    private void applyColor() {
        try {
            String raw = colorField.getText().trim().replace("#", "");
            long parsed = Long.parseLong(raw, 16);
            if (raw.length() == 6) parsed |= 0xFF000000L;
            if (raw.length() != 6 && raw.length() != 8) throw new NumberFormatException("Use RRGGBB or AARRGGBB");
            PollithiumConfig.accentColor = (int) parsed;
            colorField.setEditableColor(0xFFFFFFFF);
            PollithiumConfig.save();
        } catch (NumberFormatException ex) {
            colorField.setEditableColor(0xFFFF5555);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, 216, height, 0xDD10141E);
        context.drawText(textRenderer, title, 8, 8, 0xFFFFFFFF, false);
        context.drawText(textRenderer, "Select a widget, then drag it.", 224, 166, 0xFFCCCCCC, false);
        context.drawText(textRenderer, "Drag bottom-right corner to resize.", 224, 179, 0xFFCCCCCC, false);
        context.drawText(textRenderer, "Color: RRGGBB or AARRGGBB", 224, 72, 0xFFCCCCCC, false);

        drawWidgetPreview(context, "FPS", "FPS  120", PollithiumConfig.showFps, 8, 8, 120, 30, mouseX, mouseY);
        drawWidgetPreview(context, "PING", "PING  35 ms", PollithiumConfig.showPing, 8, 44, 130, 30, mouseX, mouseY);
        drawWidgetPreview(context, "COORDS", "XYZ  120 / 64 / -20", PollithiumConfig.showCoordinates, 8, 80, 190, 30, mouseX, mouseY);
        drawWidgetPreview(context, "DIRECTION", "FACING  N", PollithiumConfig.showDirection, 8, 116, 130, 30, mouseX, mouseY);
        drawWidgetPreview(context, "CLOCK", "TIME  12:30:00", PollithiumConfig.showClock, 8, 152, 145, 30, mouseX, mouseY);
        drawWidgetPreview(context, "CPS", "CPS  0 | 0", PollithiumConfig.showCps, 8, 188, 135, 30, mouseX, mouseY);
        drawWidgetPreview(context, "ARMOR", "ARMOR  100 100 100 100", PollithiumConfig.showArmor, 8, 224, 180, 30, mouseX, mouseY);
        drawWidgetPreview(context, "EFFECTS", "Potion effects", PollithiumConfig.showEffects, 8, 260, 180, 42, mouseX, mouseY);
        drawWidgetPreview(context, "KEYS", "W A S D", PollithiumConfig.showKeystrokes, 8, 308, 92, 52, mouseX, mouseY);

        PollithiumConfig.WidgetLayout active = layoutFor(selected);
        if (active != null) {
            int cx = active.x + active.width / 2;
            int cy = active.y + active.height / 2;
            if (Math.abs(cx - width / 2) <= 4) context.fill(width / 2, 0, width / 2 + 1, height, 0xAA38BDF8);
            if (Math.abs(cy - height / 2) <= 4) context.fill(216, height / 2, width, height / 2 + 1, 0xAA38BDF8);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawWidgetPreview(DrawContext context, String id, String sample, boolean enabled,
                                   int dx, int dy, int dw, int dh, int mouseX, int mouseY) {
        if (!enabled) return;
        PollithiumConfig.WidgetLayout l = PollithiumConfig.widget(id, dx, dy, dw, dh);
        boolean isSelected = id.equals(selected);
        int displayX = isSelected && l.x <= 216 ? width - 210 : l.x;
        int displayY = isSelected && l.x <= 216 ? height / 2 - 40 : l.y;
        int color = isSelected ? 0xCC26364A : PollithiumConfig.panelColor;
        context.fill(displayX, displayY, displayX + l.width, displayY + l.height, color);
        context.fill(displayX, displayY, displayX + l.width, displayY + 2, PollithiumConfig.accentColor);
        context.drawText(textRenderer, sample, displayX + 6, displayY + 8, 0xFFFFFFFF, false);
        if (isSelected) {
            context.fill(displayX + l.width - 7, displayY + l.height - 7, displayX + l.width, displayY + l.height, 0xFFFFFFFF);
            context.drawBorder(displayX, displayY, l.width, l.height, 0xFF38BDF8);
            context.drawText(textRenderer, "Drag / resize", displayX, displayY + l.height + 5, 0xFFFFFFFF, false);
        }
    }

    private PollithiumConfig.WidgetLayout layoutFor(String id) {
        int[] d = defaults(id);
        return PollithiumConfig.widget(id, d[0], d[1], d[2], d[3]);
    }

    private int[] defaults(String id) {
        return switch (id) {
            case "PING" -> new int[]{8, 44, 130, 30};
            case "COORDS" -> new int[]{8, 80, 190, 30};
            case "DIRECTION" -> new int[]{8, 116, 130, 30};
            case "CLOCK" -> new int[]{8, 152, 145, 30};
            case "CPS" -> new int[]{8, 188, 135, 30};
            case "ARMOR" -> new int[]{8, 224, 180, 30};
            case "EFFECTS" -> new int[]{8, 260, 180, 42};
            case "KEYS" -> new int[]{8, 308, 92, 52};
            default -> new int[]{8, 8, 120, 30};
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX > 216) {
            PollithiumConfig.WidgetLayout l = layoutFor(selected);
            int displayX = l.x <= 216 ? width - 210 : l.x;
            int displayY = l.x <= 216 ? height / 2 - 40 : l.y;
            if (mouseX >= displayX && mouseX <= displayX + l.width && mouseY >= displayY && mouseY <= displayY + l.height) {
                resizing = mouseX >= displayX + l.width - 9 && mouseY >= displayY + l.height - 9;
                dragging = !resizing;
                lastMouseX = mouseX; lastMouseY = mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if ((dragging || resizing) && button == 0) {
            PollithiumConfig.WidgetLayout l = layoutFor(selected);
            int dx = (int) Math.round(mouseX - lastMouseX);
            int dy = (int) Math.round(mouseY - lastMouseY);
            if (dragging) {
                l.x = Math.max(0, Math.min(width - l.width, snap(l.x + dx)));
                l.y = Math.max(0, Math.min(height - l.height, snap(l.y + dy)));
            } else {
                l.width = Math.max(72, Math.min(320, snap(l.width + dx)));
                l.height = Math.max(22, Math.min(180, snap(l.height + dy)));
            }
            lastMouseX = mouseX; lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private int snap(int value) { return Math.max(0, Math.round(value / 8.0f) * 8); }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && (dragging || resizing)) {
            dragging = false; resizing = false;
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
