package net.ogbonex.pollithium;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PollithiumHud {
    private static int leftCps, rightCps, leftClicks, rightClicks;
    private static boolean leftWasDown, rightWasDown;
    private static long cpsWindowStart = System.currentTimeMillis();

    private PollithiumHud() {}

    public static void tick(MinecraftClient client) {
        if (client.getWindow() == null) return;
        boolean leftDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;
        if (leftDown && !leftWasDown) leftClicks++;
        if (rightDown && !rightWasDown) rightClicks++;
        leftWasDown = leftDown;
        rightWasDown = rightDown;
        long now = System.currentTimeMillis();
        if (now - cpsWindowStart >= 1000) {
            leftCps = leftClicks; rightCps = rightClicks;
            leftClicks = 0; rightClicks = 0; cpsWindowStart = now;
        }
    }

    public static void render(DrawContext context, MinecraftClient client) {
        if (client.player == null) return;
        if (PollithiumConfig.showFps) draw(context, client, "FPS", "FPS  " + client.getCurrentFps(), 8, 8, 120, 30);
        if (PollithiumConfig.showPing) {
            int ping = 0;
            if (client.getNetworkHandler() != null) {
                PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            draw(context, client, "PING", "PING  " + ping + " ms", 8, 44, 130, 30);
        }
        if (PollithiumConfig.showCoordinates) {
            String value = String.format("XYZ  %.1f / %.1f / %.1f", client.player.getX(), client.player.getY(), client.player.getZ());
            draw(context, client, "COORDS", value, 8, 80, 190, 30);
        }
        if (PollithiumConfig.showDirection) {
            float yaw = (client.player.getYaw() % 360 + 360) % 360;
            String[] dirs = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
            draw(context, client, "DIRECTION", "FACING  " + dirs[Math.round(yaw / 45f) % 8], 8, 116, 130, 30);
        }
        if (PollithiumConfig.showClock) {
            draw(context, client, "CLOCK", "TIME  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 8, 152, 145, 30);
        }
        if (PollithiumConfig.showCps) draw(context, client, "CPS", "CPS  " + leftCps + " | " + rightCps, 8, 188, 135, 30);
        if (PollithiumConfig.showArmor) draw(context, client, "ARMOR", "ARMOR  " + armorSummary(client), 8, 224, 180, 30);
        if (PollithiumConfig.showEffects) {
            List<String> effects = new ArrayList<>();
            for (StatusEffectInstance effect : client.player.getStatusEffects()) {
                if (effects.size() >= 4) break;
                String name = effect.getEffectType().value().getName().getString();
                effects.add(name + " " + (effect.getAmplifier() + 1) + "  " + formatDuration(effect.getDuration()));
            }
            if (!effects.isEmpty()) draw(context, client, "EFFECTS", effects, 8, 260, 180, Math.max(34, 18 + effects.size() * 12));
        }
        if (PollithiumConfig.showKeystrokes) drawKeys(context, client);
    }

    private static void draw(DrawContext context, MinecraftClient client, String id, String value, int defaultX, int defaultY, int defaultW, int defaultH) {
        draw(context, client, id, List.of(value), defaultX, defaultY, defaultW, defaultH);
    }

    private static void draw(DrawContext context, MinecraftClient client, String id, List<String> lines, int defaultX, int defaultY, int defaultW, int defaultH) {
        PollithiumConfig.WidgetLayout layout = PollithiumConfig.widget(id, defaultX, defaultY, defaultW, defaultH);
        int x = layout.x, y = layout.y, width = layout.width, height = layout.height;
        context.fill(x, y, x + width, y + height, PollithiumConfig.panelColor);
        context.fill(x, y, x + width, y + 2, PollithiumConfig.accentColor);
        int lineY = y + 7;
        for (String line : lines) {
            if (lineY + 9 > y + height) break;
            context.drawText(client.textRenderer, line, x + 6, lineY, 0xFFFFFFFF, false);
            lineY += 12;
        }
    }

    private static void drawKeys(DrawContext context, MinecraftClient client) {
        PollithiumConfig.WidgetLayout layout = PollithiumConfig.widget("KEYS", 8, 296, 92, 52);
        int x = layout.x, y = layout.y;
        context.fill(x, y, x + layout.width, y + layout.height, PollithiumConfig.panelColor);
        context.fill(x, y, x + layout.width, y + 2, PollithiumConfig.accentColor);
        drawKey(context, client, "W", x + layout.width / 2 - 5, y + 7, key(client, GLFW.GLFW_KEY_W));
        drawKey(context, client, "A", x + layout.width / 2 - 17, y + 23, key(client, GLFW.GLFW_KEY_A));
        drawKey(context, client, "S", x + layout.width / 2 - 5, y + 23, key(client, GLFW.GLFW_KEY_S));
        drawKey(context, client, "D", x + layout.width / 2 + 7, y + 23, key(client, GLFW.GLFW_KEY_D));
    }

    private static String armorSummary(MinecraftClient client) {
        StringBuilder out = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = client.player.getInventory().getArmorStack(i);
            if (stack.isEmpty()) out.append("--");
            else if (stack.isDamageable()) out.append(Math.max(0, stack.getMaxDamage() - stack.getDamage()));
            else out.append("OK");
            if (i > 0) out.append(" ");
        }
        return out.toString();
    }

    private static String formatDuration(int ticks) {
        if (ticks < 0) return "∞";
        int seconds = ticks / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private static boolean key(MinecraftClient client, int key) {
        return GLFW.glfwGetKey(client.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String label, int x, int y, boolean pressed) {
        context.fill(x, y, x + 10, y + 10, pressed ? PollithiumConfig.accentColor : 0xCC252B38);
        context.drawText(client.textRenderer, label, x + 3, y + 1, 0xFFFFFFFF, false);
    }

    public static void renderCustomCrosshair(DrawContext context, MinecraftClient client) {
        int cx = client.getWindow().getScaledWidth() / 2, cy = client.getWindow().getScaledHeight() / 2;
        int color = PollithiumConfig.accentColor;
        context.fill(cx - 7, cy, cx - 2, cy + 1, color);
        context.fill(cx + 2, cy, cx + 7, cy + 1, color);
        context.fill(cx, cy - 7, cx + 1, cy - 2, color);
        context.fill(cx, cy + 2, cx + 1, cy + 7, color);
    }
}
