package net.ogbonex.pollithium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class PollithiumClient implements ClientModInitializer {
    public static final String MOD_ID = "pollithium";
    private static KeyBinding toggleHud;
    private static KeyBinding openEditor;

    @Override
    public void onInitializeClient() {
        PollithiumConfig.load();
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pollithium.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8,
                KeyBinding.Category.MISC));
        openEditor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pollithium.open_editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                PollithiumConfig.hudEnabled = !PollithiumConfig.hudEnabled;
                PollithiumConfig.save();
            }
            while (openEditor.wasPressed()) {
                if (client.player != null) client.setScreen(new HudEditorScreen());
            }
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT,
                Identifier.of(MOD_ID, "main_hud"), (drawContext, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null && PollithiumConfig.hudEnabled) {
                        PollithiumHud.render(drawContext, client);
                    }
                });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> PollithiumConfig.save());
    }
}
