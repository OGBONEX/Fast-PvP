package net.ogbonex.pollithium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class PollithiumClient implements ClientModInitializer {
    public static final String MOD_ID = "pollithium";
    private static KeyBinding toggleHud, openEditor, toggleSprint, zoom;
    private static boolean sprintToggled;
    private static double previousFov = 70;
    private static boolean zooming;

    @Override
    public void onInitializeClient() {
        PollithiumConfig.load();
        toggleHud = register("key.pollithium.toggle_hud", GLFW.GLFW_KEY_F8);
        openEditor = register("key.pollithium.open_editor", GLFW.GLFW_KEY_RIGHT_SHIFT);
        toggleSprint = register("key.pollithium.toggle_sprint", GLFW.GLFW_KEY_UNKNOWN);
        zoom = register("key.pollithium.zoom", GLFW.GLFW_KEY_C);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                PollithiumConfig.hudEnabled = !PollithiumConfig.hudEnabled;
                PollithiumConfig.save();
            }
            while (openEditor.wasPressed()) {
                if (client.player != null) client.setScreen(new HudEditorScreen());
            }
            while (toggleSprint.wasPressed()) sprintToggled = !sprintToggled;
            if (client.player != null && PollithiumConfig.toggleSprintEnabled) {
                client.player.setSprinting(sprintToggled && client.player.forwardSpeed > 0);
            }
            if (client.player == null) sprintToggled = false;

            boolean wantsZoom = zoom.isPressed() && client.player != null;
            if (wantsZoom && !zooming) {
                previousFov = client.options.getFov().getValue();
                zooming = true;
            }
            if (zooming && wantsZoom) {
                client.options.getFov().setValue(30);
            } else if (zooming) {
                client.options.getFov().setValue((int) previousFov);
                zooming = false;
                previousFov = 70;
            }
            PollithiumHud.tick(client);
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT,
                Identifier.of(MOD_ID, "main_hud"), (drawContext, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        if (PollithiumConfig.hudEnabled) PollithiumHud.render(drawContext, client);
                        if (PollithiumConfig.showCrosshair) PollithiumHud.renderCustomCrosshair(drawContext, client);
                    }
                });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> PollithiumConfig.save());
    }

    private static KeyBinding register(String translationKey, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey, InputUtil.Type.KEYSYM, key, KeyBinding.Category.MISC));
    }
}
