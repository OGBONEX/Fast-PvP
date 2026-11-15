package net.ogbonex.pollithium;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public final class HudEditorScreen extends Screen {
    private static final Map<String, String> WIDGETS = new LinkedHashMap<>();
    private static final int[] PALETTE = {
            0xFF8B5CF6, 0xFF3B82F6, 0xFF06B6D4, 0xFF10B981,
            0xFF84CC16, 0xFFFACC15, 0xFFF97316, 0xFFEF4444,
            0xFFEC4899, 0xFFFFFFFF, 0xFF9CA3AF, 0xFF111827
    };
    private static final String[] PALETTE_LABELS = {
            "P", "B", "C", "G", "L", "Y", "O", "R", "M", "W", "S", "K"
    };

    static {
        WIDGETS.put("FPS", "FPS");
        WIDGETS.put("PING", "Ping");
        WIDGETS.put("COORDS", "XYZ");
        WIDGETS.put("DIRECTION", "Facing");
        WIDGETS.put("CLOCK", "Clock");
        WIDGETS.put("CPS", "CPS");
        WIDGETS.put("ARMOR", "Armor");
        WIDGETS.put("EFFECTS", "Effects");
        WIDGETS.put("KEYS", "Keys");
        WIDGETS.put("BIOME", "Biome");
        WIDGETS.put("DIMENSION", "Dimension");
        WIDGETS.put("WORLDTIME", "World time");
        WIDGETS.put("MEMORY", "Memory");
    }

    private String selected = "FPS";
    private boolean dragging;
    private boolean resizing;
    private int previewOffsetX;
    private int previewOffsetY;
    private TextFieldWidget accentField;
    private TextFieldWidget panelField;
    private int sidebarWidth;
    private int rightX;
    private int rightWidth;
    private int previewX;
    private int previewY;

    public HudEditorScreen() {
        super(Text.literal("Pollithium HUD Editor"));
    }

    @Override
    protected void init() {
        sidebarWidth = Math.min(216, Math.max(144, width / 2));
        rightX = sidebarWidth + 8;
        rightWidth = Math.max(72, width - rightX - 8);
        previewX = rightX;
        previewY = Math.max(188, height - 58);

        contextPanel();
        addToggle("FPS", () -> PollithiumConfig.showFps = !PollithiumConfig.showFps, () -> PollithiumConfig.showFps, 0);
        addToggle("Ping", () -> PollithiumConfig.showPing = !PollithiumConfig.showPing, () -> PollithiumConfig.showPing, 1);
        addToggle("XYZ", () -> PollithiumConfig.showCoordinates = !PollithiumConfig.showCoordinates, () -> PollithiumConfig.showCoordinates, 2);
        addToggle("Keys", () -> PollithiumConfig.showKeystrokes = !PollithiumConfig.showKeystrokes, () -> PollithiumConfig.showKeystrokes, 3);
        addToggle("CPS", () -> PollithiumConfig.showCps = !PollithiumConfig.showCps, () -> PollithiumConfig.showCps, 4);
        addToggle("Armor", () -> PollithiumConfig.showArmor = !PollithiumConfig.showArmor, () -> PollithiumConfig.showArmor, 5);
        addToggle("Effects", () -> PollithiumConfig.showEffects = !PollithiumConfig.showEffects, () -> PollithiumConfig.showEffects, 6);
        addToggle("Clock", () -> PollithiumConfig.showClock = !PollithiumConfig.showClock, () -> PollithiumConfig.showClock, 7);
        addToggle("Facing", () -> PollithiumConfig.showDirection = !PollithiumConfig.showDirection, () -> PollithiumConfig.showDirection, 8);
        addToggle("Low fire", () -> PollithiumConfig.lowFireEnabled = !PollithiumConfig.lowFireEnabled, () -> PollithiumConfig.lowFireEnabled, 9);
        addToggle("Shulker", () -> PollithiumConfig.shulkerBoxTooltipEnabled = !PollithiumConfig.shulkerBoxTooltipEnabled, () -> PollithiumConfig.shulkerBoxTooltipEnabled, 10);
        addToggle("Crosshair", () -> PollithiumConfig.showCrosshair = !PollithiumConfig.showCrosshair, () -> PollithiumConfig.showCrosshair, 11);
        addToggle("Sprint", () -> PollithiumConfig.toggleSprintEnabled = !PollithiumConfig.toggleSprintEnabled, () -> PollithiumConfig.toggleSprintEnabled, 12);
        addToggle("Biome", () -> PollithiumConfig.showBiome = !PollithiumConfig.showBiome, () -> PollithiumConfig.showBiome, 13);
        addToggle("Dimension", () -> PollithiumConfig.showDimension = !PollithiumConfig.showDimension, () -> PollithiumConfig.showDimension, 14);
        addToggle("World time", () -> PollithiumConfig.showWorldTime = !PollithiumConfig.showWorldTime, () -> PollithiumConfig.showWorldTime, 15);
        addToggle("Memory", () -> PollithiumConfig.showMemory = !PollithiumConfig.showMemory, () -> PollithiumConfig.showMemory, 16);

        addDrawableChild(ButtonWidget.builder(Text.literal("Widget: " + WIDGETS.get(selected)), button -> {
            String[] ids = WIDGETS.keySet().toArray(String[]::new);
            int index = java.util.Arrays.asList(ids).indexOf(selected);
            selected = ids[(index + 1) % ids.length];
            previewOffsetX = 0;
            previewOffsetY = 0;
            button.setMessage(Text.literal("Widget: " + WIDGETS.get(selected)));
        }).dimensions(rightX, 26, rightWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset selected"), button -> {
            PollithiumConfig.widgets.remove(selected);
            previewOffsetX = 0;
            previewOffsetY = 0;
            PollithiumConfig.save();
        }).dimensions(rightX, 50, rightWidth, 20).build());

        int fieldWidth = Math.max(40, rightWidth - 48);
        accentField = new TextFieldWidget(textRenderer, rightX, 80, fieldWidth, 20, Text.literal("Theme HEX"));
        accentField.setMaxLength(8);
        accentField.setText(String.format("%08X", PollithiumConfig.accentColor));
        accentField.setPlaceholder(Text.literal("AARRGGBB"));
        addDrawableChild(accentField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Set"), button -> applyHex(accentField, true))
                .dimensions(rightX + fieldWidth + 4, 80, Math.max(40, rightWidth - fieldWidth - 4), 20).build());

        panelField = new TextFieldWidget(textRenderer, rightX, 106, fieldWidth, 20, Text.literal("Panel HEX"));
        panelField.setMaxLength(8);
        panelField.setText(String.format("%08X", PollithiumConfig.panelColor));
        panelField.setPlaceholder(Text.literal("AARRGGBB"));
        addDrawableChild(panelField);
        addDrawableChild(ButtonWidget.builder(Text.literal("Set"), button -> applyHex(panelField, false))
                .dimensions(rightX + fieldWidth + 4, 106, Math.max(40, rightWidth - fieldWidth - 4), 20).build());

        for (int i = 0; i < PALETTE.length; i++) {
            final int color = PALETTE[i];
            int col = i % 6;
            int row = i / 6;
            int cellWidth = Math.max(12, (rightWidth - 20) / 6);
            addDrawableChild(ButtonWidget.builder(Text.literal(PALETTE_LABELS[i]), button -> {
                applyTheme(color);
                accentField.setText(String.format("%08X", color));
            }).dimensions(rightX + col * (cellWidth + 3), 136 + row * 22, cellWidth, 19).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Apply theme colour"), button -> applyThemeFromField())
                .dimensions(rightX, 184, rightWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset positions"), button -> {
            PollithiumConfig.widgets.clear();
            previewOffsetX = 0;
            previewOffsetY = 0;
            PollithiumConfig.save();
        }).dimensions(rightX, 208, rightWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions(rightX, 232, rightWidth, 20).build());
    }

    private void contextPanel() {
        // Keeps the editor layout calculation in one place for narrow GUI scales.
    }

    private void addToggle(String name, Runnable change, BooleanSupplier state, int index) {
        int col = index % 2;
        int row = index / 2;
        int cellWidth = Math.max(56, (sidebarWidth - 22) / 2);
        int x = 8 + col * (cellWidth + 6);
        int y = 28 + row * 22;
        addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (state.getAsBoolean() ? "ON" : "OFF")), button -> {
            change.run();
            button.setMessage(Text.literal(name + ": " + (state.getAsBoolean() ? "ON" : "OFF")));
            PollithiumConfig.save();
        }).dimensions(x, y, cellWidth, 19).build());
    }

    private void applyThemeFromField() {
        try {
            String raw = accentField.getText().trim().replace("#", "");
            if (raw.length() != 6 && raw.length() != 8) throw new NumberFormatException();
            long parsed = Long.parseLong(raw, 16);
            if (raw.length() == 6) parsed |= 0xFF000000L;
            applyTheme((int) parsed);
            accentField.setEditableColor(0xFFFFFFFF);
        } catch (NumberFormatException exception) {
            accentField.setEditableColor(0xFFFF5555);
        }
    }

    private void applyTheme(int color) {
        PollithiumConfig.accentColor = color;
        int red = (color >> 16 & 0xFF) / 7;
        int green = (color >> 8 & 0xFF) / 7;
        int blue = (color & 0xFF) / 7;
        PollithiumConfig.panelColor = 0xB0000000 | red << 16 | green << 8 | blue;
        PollithiumConfig.save();
        if (accentField != null) accentField.setText(String.format("%08X", color));
        if (panelField != null) panelField.setText(String.format("%08X", PollithiumConfig.panelColor));
    }

    private void applyHex(TextFieldWidget field, boolean accent) {
        try {
            String raw = field.getText().trim().replace("#", "");
            if (raw.length() != 6 && raw.length() != 8) throw new NumberFormatException();
            long parsed = Long.parseLong(raw, 16);
            if (raw.length() == 6) parsed |= 0xFF000000L;
            if (accent) PollithiumConfig.accentColor = (int) parsed;
            else PollithiumConfig.panelColor = (int) parsed;
            field.setEditableColor(0xFFFFFFFF);
            PollithiumConfig.save();
        } catch (NumberFormatException exception) {
            field.setEditableColor(0xFFFF5555);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, sidebarWidth, height, 0xDD10141E);
        context.fill(rightX - 4, 0, width, height, 0xAA080B12);
        context.drawText(textRenderer, title, 8, 8, 0xFFFFFFFF, false);
        context.drawText(textRenderer, "Colors", rightX, 72, 0xFFCCCCCC, false);
        context.drawText(textRenderer, "Accent palette (HEX also supported)", rightX, 126, 0xFFCCCCCC, false);
        context.drawText(textRenderer, "Selected widget preview", rightX, Math.max(170, height - 78), 0xFFCCCCCC, false);

        PollithiumConfig.WidgetLayout layout = layoutFor(selected);
        int boxWidth = Math.min(layout.width, rightWidth);
        int boxHeight = Math.min(layout.height, 48);
        int x = previewX + previewOffsetX;
        int y = previewY + previewOffsetY;
        x = Math.max(rightX, Math.min(width - boxWidth - 4, x));
        y = Math.max(Math.max(170, height - 72), Math.min(height - boxHeight - 6, y));
        boolean enabled = isEnabled(selected);
        context.fill(x, y, x + boxWidth, y + boxHeight, enabled ? PollithiumConfig.panelColor : 0x66444444);
        context.fill(x, y, x + boxWidth, y + 2, PollithiumConfig.accentColor);
        context.drawText(textRenderer, WIDGETS.get(selected), x + 6, y + 8, enabled ? 0xFFFFFFFF : 0xFF999999, false);
        context.fill(x + boxWidth - 7, y + boxHeight - 7, x + boxWidth, y + boxHeight, 0xFFFFFFFF);
        context.drawStrokedRectangle(x, y, boxWidth, boxHeight, 0xFF38BDF8);

        int centerX = rightX + rightWidth / 2;
        int centerY = Math.max(170, height - 72) + Math.max(0, height - Math.max(170, height - 72)) / 2;
        if (Math.abs((x + boxWidth / 2) - centerX) <= 4) {
            context.fill(centerX, Math.max(170, height - 72), centerX + 1, height - 4, 0xAA38BDF8);
        }
        if (Math.abs((y + boxHeight / 2) - centerY) <= 4) {
            context.fill(rightX, centerY, width - 4, centerY + 1, 0xAA38BDF8);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private boolean isEnabled(String id) {
        return switch (id) {
            case "FPS" -> PollithiumConfig.showFps;
            case "PING" -> PollithiumConfig.showPing;
            case "COORDS" -> PollithiumConfig.showCoordinates;
            case "DIRECTION" -> PollithiumConfig.showDirection;
            case "CLOCK" -> PollithiumConfig.showClock;
            case "CPS" -> PollithiumConfig.showCps;
            case "ARMOR" -> PollithiumConfig.showArmor;
            case "EFFECTS" -> PollithiumConfig.showEffects;
            case "KEYS" -> PollithiumConfig.showKeystrokes;
            case "BIOME" -> PollithiumConfig.showBiome;
            case "DIMENSION" -> PollithiumConfig.showDimension;
            case "WORLDTIME" -> PollithiumConfig.showWorldTime;
            case "MEMORY" -> PollithiumConfig.showMemory;
            default -> true;
        };
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
            case "BIOME" -> new int[]{8, 368, 180, 30};
            case "DIMENSION" -> new int[]{8, 404, 180, 30};
            case "WORLDTIME" -> new int[]{8, 440, 180, 30};
            case "MEMORY" -> new int[]{8, 476, 190, 30};
            default -> new int[]{8, 8, 120, 30};
        };
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            PollithiumConfig.WidgetLayout layout = layoutFor(selected);
            int boxWidth = Math.min(layout.width, rightWidth);
            int boxHeight = Math.min(layout.height, 48);
            int x = Math.max(rightX, Math.min(width - boxWidth - 4, previewX + previewOffsetX));
            int y = Math.max(Math.max(170, height - 72), Math.min(height - boxHeight - 6, previewY + previewOffsetY));
            if (click.x() >= x && click.x() <= x + boxWidth && click.y() >= y && click.y() <= y + boxHeight) {
                resizing = click.x() >= x + boxWidth - 9 && click.y() >= y + boxHeight - 9;
                dragging = !resizing;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if ((dragging || resizing) && click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            PollithiumConfig.WidgetLayout layout = layoutFor(selected);
            int dx = (int) Math.round(offsetX);
            int dy = (int) Math.round(offsetY);
            if (dragging) {
                layout.x = Math.max(0, snap(layout.x + dx));
                layout.y = Math.max(0, snap(layout.y + dy));
                previewOffsetX = Math.max(0, Math.min(rightWidth - 72, previewOffsetX + dx));
                previewOffsetY = Math.max(0, Math.min(48, previewOffsetY + dy));
            } else {
                layout.width = Math.max(72, Math.min(320, snap(layout.width + dx)));
                layout.height = Math.max(22, Math.min(180, snap(layout.height + dy)));
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    private int snap(int value) {
        return Math.max(0, Math.round(value / 8.0f) * 8);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1 && (dragging || resizing)) {
            dragging = false;
            resizing = false;
            PollithiumConfig.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        PollithiumConfig.save();
        if (client != null) client.setScreen(null);
    }
}
