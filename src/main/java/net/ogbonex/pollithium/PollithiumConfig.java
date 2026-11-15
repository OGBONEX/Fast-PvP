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
    public static boolean hudEnabled=true;
    public static boolean showFps=true, showCoordinates=true, showPing=true, showKeystrokes=true, showCps=true;
    public static boolean showArmor=true, showEffects=true, showClock=false, showDirection=true, showCrosshair=false;
    public static boolean toggleSprintEnabled=false, lowFireEnabled=true, shulkerBoxTooltipEnabled=true;
        public static int hudX=8, hudY=8, accentColor=0xFF8B5CF6, panelColor=0x990D111B;
    private PollithiumConfig() {}
    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader reader=Files.newBufferedReader(FILE)) {
            Settings s=GSON.fromJson(reader,Settings.class);
            if(s==null)return;
            hudEnabled=s.hudEnabled; showFps=s.showFps; showCoordinates=s.showCoordinates; showPing=s.showPing;
            showKeystrokes=s.showKeystrokes; showCps=s.showCps; showArmor=s.showArmor; showEffects=s.showEffects;
            showClock=s.showClock; showDirection=s.showDirection; showCrosshair=s.showCrosshair;
            toggleSprintEnabled=s.toggleSprintEnabled; lowFireEnabled=s.lowFireEnabled;
            shulkerBoxTooltipEnabled=s.shulkerBoxTooltipEnabled;
hudX=Math.max(0,s.hudX); hudY=Math.max(0,s.hudY);
            accentColor=s.accentColor; panelColor=s.panelColor;
        } catch(Exception e){System.err.println("[Pollithium] Could not load config: "+e.getMessage());}
    }
    public static void save() {
        try {
            Files.createDirectories(FILE.getParent()); Settings s=new Settings();
            s.hudEnabled=hudEnabled; s.showFps=showFps; s.showCoordinates=showCoordinates; s.showPing=showPing;
            s.showKeystrokes=showKeystrokes; s.showCps=showCps; s.showArmor=showArmor; s.showEffects=showEffects;
            s.showClock=showClock; s.showDirection=showDirection; s.showCrosshair=showCrosshair;
            s.toggleSprintEnabled=toggleSprintEnabled; s.lowFireEnabled=lowFireEnabled;
            s.shulkerBoxTooltipEnabled=shulkerBoxTooltipEnabled;
s.hudX=hudX; s.hudY=hudY;
            s.accentColor=accentColor; s.panelColor=panelColor;
            try(Writer writer=Files.newBufferedWriter(FILE)){GSON.toJson(s,writer);}
        } catch(Exception e){System.err.println("[Pollithium] Could not save config: "+e.getMessage());}
    }
    private static final class Settings {
        boolean hudEnabled=true,showFps=true,showCoordinates=true,showPing=true,showKeystrokes=true,showCps=true;
        boolean showArmor=true,showEffects=true,showClock=false,showDirection=true,showCrosshair=false;
        boolean toggleSprintEnabled=false,lowFireEnabled=true,shulkerBoxTooltipEnabled=true;
        int hudX=8,hudY=8,accentColor=0xFF8B5CF6,panelColor=0x990D111B;
    }
}