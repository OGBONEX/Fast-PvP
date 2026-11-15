package net.ogbonex.pollithium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PollithiumConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pollithium.json");

    public static boolean hudEnabled = true;
    public static boolean showFps = true;
    public static boolean showCoordinates = true;
    public static boolean showPing = true;
    public static boolean showKeystrokes = true;
    public static int hudX = 8;
    public static int hudY = 8;

    private PollithiumConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Settings s = GSON.fromJson(reader, Settings.class);
            if (s == null) return;
            hudEnabled = s.hudEnabled;
            showFps = s.showFps;
            showCoordinates = s.showCoordinates;
            showPing = s.showPing;
            showKeystrokes = s.showKeystrokes;
            hudX = Math.max(0, s.hudX);
            hudY = Math.max(0, s.hudY);
        } catch (Exception e) {
            System.err.println("[Pollithium] Could not load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Settings s = new Settings();
            s.hudEnabled = hudEnabled;
            s.showFps = showFps;
            s.showCoordinates = showCoordinates;
            s.showPing = showPing;
            s.showKeystrokes = showKeystrokes;
            s.hudX = hudX;
            s.hudY = hudY;
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(s, writer);
            }
        } catch (Exception e) {
            System.err.println("[Pollithium] Could not save config: " + e.getMessage());
        }
    }

    private static final class Settings {
        boolean hudEnabled = true, showFps = true, showCoordinates = true;
        boolean showPing = true, showKeystrokes = true;
        int hudX = 8, hudY = 8;
    }
}
