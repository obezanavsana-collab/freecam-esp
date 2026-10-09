package com.example.fcesp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FreecamEspMod implements ClientModInitializer {
    public static KeyBinding freecamKey;
    public static KeyBinding espKey;

    @Override
    public void onInitializeClient() {
        freecamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fcesp.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, KeyBinding.Category.MISC));
        espKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fcesp.esp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (freecamKey.wasPressed()) Freecam.toggle();
            while (espKey.wasPressed()) Esp.enabled = !Esp.enabled;
        });

        WorldRenderEvents.LAST.register(Esp::render);
    }
}
