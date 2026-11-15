package net.ogbonex.pollithium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PollithiumConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pollithium.json");

    public static boolean hudEnabled = true;
    public static boolean showFps = true, showCoordinates = true, showPing = true, showKeystrokes = true, showCps = true;
    public static boolean showArmor = true, showEffects = true, showClock = false, showDirection = true, showCrosshair = false;
        boolean showBiome = false, showDimension = false, showWorldTime = false, showMemory = false;
    public static boolean showBiome = true, showDimension = true, showWorldTime = false, showMemory = false;
    public static boolean toggleSprintEnabled = false, lowFireEnabled = true, shulkerBoxTooltipEnabled = true;
    public static int hudX = 8, hudY = 8, accentColor = 0xFF8B5CF6, panelColor = 0x990D111B;
    public static final Map<String, WidgetLayout> widgets = new LinkedHashMap<>();

    private PollithiumConfig() {}

    public static WidgetLayout widget(String id, int x, int y, int width, int height) {
        return widgets.computeIfAbsent(id, key -> new WidgetLayout(x, y, width, height));
    }

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Settings s = GSON.fromJson(reader, Settings.class);
            if (s == null) return;
            hudEnabled = s.hudEnabled;
            showFps = s.showFps; showCoordinates = s.showCoordinates; showPing = s.showPing;
            showKeystrokes = s.showKeystrokes; showCps = s.showCps; showArmor = s.showArmor;
            showEffects = s.showEffects; showClock = s.showClock; showDirection = s.showDirection;
            showBiome = s.showBiome; showDimension = s.showDimension; showWorldTime = s.showWorldTime; showMemory = s.showMemory;
            showCrosshair = s.showCrosshair; toggleSprintEnabled = s.toggleSprintEnabled;
            lowFireEnabled = s.lowFireEnabled; shulkerBoxTooltipEnabled = s.shulkerBoxTooltipEnabled;
            hudX = Math.max(0, s.hudX); hudY = Math.max(0, s.hudY);
            accentColor = s.accentColor; panelColor = s.panelColor;
            widgets.clear();
            if (s.widgets != null) {
                s.widgets.forEach((id, layout) -> {
                    if (layout != null) widgets.put(id, layout.clamped());
                });
            }
        } catch (Exception e) {
            System.err.println("[Pollithium] Could not load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Settings s = new Settings();
            s.hudEnabled = hudEnabled;
            s.showFps = showFps; s.showCoordinates = showCoordinates; s.showPing = showPing;
            s.showKeystrokes = showKeystrokes; s.showCps = showCps; s.showArmor = showArmor;
            s.showEffects = showEffects; s.showClock = showClock; s.showDirection = showDirection;
            s.showBiome = showBiome; s.showDimension = showDimension; s.showWorldTime = showWorldTime; s.showMemory = showMemory;
            s.showCrosshair = showCrosshair; s.toggleSprintEnabled = toggleSprintEnabled;
            s.lowFireEnabled = lowFireEnabled; s.shulkerBoxTooltipEnabled = shulkerBoxTooltipEnabled;
            s.hudX = hudX; s.hudY = hudY; s.accentColor = accentColor; s.panelColor = panelColor;
            s.widgets = new LinkedHashMap<>(widgets);
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(s, writer);
            }
        } catch (Exception e) {
            System.err.println("[Pollithium] Could not save config: " + e.getMessage());
        }
    }

    public static final class WidgetLayout {
        public int x, y, width, height;
        public WidgetLayout() { this(8, 8, 150, 30); }
        public WidgetLayout(int x, int y, int width, int height) {
            this.x = Math.max(0, x); this.y = Math.max(0, y);
            this.width = Math.max(72, Math.min(320, width));
            this.height = Math.max(22, Math.min(180, height));
        }
        public WidgetLayout clamped() { return new WidgetLayout(x, y, width, height); }
    }

    private static final class Settings {
        boolean hudEnabled = true;
        boolean showFps = true, showCoordinates = true, showPing = true, showKeystrokes = true, showCps = true;
        boolean showArmor = true, showEffects = true, showClock = false, showDirection = true, showCrosshair = false;
        boolean toggleSprintEnabled = false, lowFireEnabled = true, shulkerBoxTooltipEnabled = true;
        int hudX = 8, hudY = 8, accentColor = 0xFF8B5CF6, panelColor = 0x990D111B;
        Map<String, WidgetLayout> widgets = new LinkedHashMap<>();
    }
}
