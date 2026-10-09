package com.example.fcesp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class Freecam {
    public static boolean enabled = false;
    private static FreeCameraEntity camera;
    private static Entity originalCamera;

    public static void toggle() {
        if (enabled) disable(); else enable();
    }

    public static void enable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        originalCamera = mc.getCameraEntity();
        camera = new FreeCameraEntity();
        mc.world.addEntity(camera);
        mc.setCameraEntity(camera);
        enabled = true;
    }

    public static void disable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (camera != null && mc.world != null) {
            mc.world.removeEntity(camera.getId(), Entity.RemovalReason.DISCARDED);
            camera = null;
        }
        if (originalCamera != null) mc.setCameraEntity(originalCamera);
        enabled = false;
    }
}
