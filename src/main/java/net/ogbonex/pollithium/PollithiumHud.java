package net.ogbonex.pollithium;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public final class PollithiumHud {
    private PollithiumHud() {}

    public static void render(DrawContext context, MinecraftClient client) {
        int x = PollithiumConfig.hudX;
        int y = PollithiumConfig.hudY;
        int width = 126;
        int lines = 0;
        if (PollithiumConfig.showFps) lines++;
        if (PollithiumConfig.showPing) lines++;
        if (PollithiumConfig.showCoordinates) lines++;
        if (PollithiumConfig.showKeystrokes) lines += 2;
        if (lines == 0) return;

        int height = lines * 12 + 8;
        context.fill(x, y, x + width, y + height, 0x990D111B);
        context.fill(x, y, x + width, y + 2, 0xFF8B5CF6);

        int textY = y + 6;
        if (PollithiumConfig.showFps) {
            context.drawText(client.textRenderer, "FPS  " + client.getCurrentFps(),
                    x + 7, textY, 0xFFFFFFFF, false);
            textY += 12;
        }
        if (PollithiumConfig.showPing) {
            int ping = 0;
            if (client.getNetworkHandler() != null && client.player != null) {
                PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            context.drawText(client.textRenderer, "PING  " + ping + " ms",
                    x + 7, textY, 0xFFFFFFFF, false);
            textY += 12;
        }
        if (PollithiumConfig.showCoordinates && client.player != null) {
            context.drawText(client.textRenderer,
                    String.format("XYZ  %.1f / %.1f / %.1f",
                            client.player.getX(), client.player.getY(), client.player.getZ()),
                    x + 7, textY, 0xFFFFFFFF, false);
            textY += 12;
        }
        if (PollithiumConfig.showKeystrokes) {
            boolean w = key(client, GLFW.GLFW_KEY_W);
            boolean a = key(client, GLFW.GLFW_KEY_A);
            boolean s = key(client, GLFW.GLFW_KEY_S);
            boolean d = key(client, GLFW.GLFW_KEY_D);
            drawKey(context, client, "W", x + 49, textY, w);
            textY += 12;
            drawKey(context, client, "A", x + 37, textY, a);
            drawKey(context, client, "S", x + 49, textY, s);
            drawKey(context, client, "D", x + 61, textY, d);
        }
    }

    private static boolean key(MinecraftClient client, int key) {
        return GLFW.glfwGetKey(client.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    private static void drawKey(DrawContext context, MinecraftClient client,
                                String label, int x, int y, boolean pressed) {
        int bg = pressed ? 0xFF8B5CF6 : 0xCC252B38;
        context.fill(x, y, x + 10, y + 10, bg);
        int textWidth = client.textRenderer.getWidth(label);
        context.drawText(client.textRenderer, label, x + (10 - textWidth) / 2,
                y + 1, 0xFFFFFFFF, false);
    }
}
