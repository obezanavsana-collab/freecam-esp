package com.example.fcesp;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderSystem;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Esp {
    public static boolean enabled = true;

    public static void render(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!enabled || mc.world == null) return;

        Camera camera = context.camera();
        Vec3d cam = camera.getPos();
        var matrices = context.matrixStack();
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        Matrix4f m = matrices.peek().getPositionMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // --- Сундуки, шалкеры, баррели, эндер-сундуки ---
        for (BlockEntity be : mc.world.blockEntities()) {
            boolean chest = be instanceof ChestBlockEntity
                    || be instanceof ShulkerBoxBlockEntity
                    || be instanceof BarrelBlockEntity
                    || be instanceof EnderChestBlockEntity;
            if (!chest) continue;
            if (be.getPos().getSquaredDistance(cam) > 64 * 64) continue; // лимит дальности

            Box box = new Box(be.getPos()).expand(0.03);
            float[] c = be instanceof ShulkerBoxBlockEntity
                    ? new float[]{0.7f, 0.3f, 0.9f}   // шалкеры - фиолетовые
                    : new float[]{1.0f, 0.6f, 0.0f};  // сундуки - оранжевые
            drawBox(buffer, m, box, c[0], c[1], c[2], 1.0f);
        }

        // --- Выброшенные предметы ---
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof ItemEntity)) continue;
            if (e.squaredDistanceTo(cam) > 48 * 48) continue;
            drawBox(buffer, m, e.getBoundingBox().expand(0.08), 0.2f, 0.8f, 1.0f, 1.0f);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest(); // сквозь стены
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    private static void line(BufferBuilder b, Matrix4f m,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float bl, float a) {
        b.vertex(m, (float) x1, (float) y1, (float) z1).color(r, g, bl, a);
        b.vertex(m, (float) x2, (float) y2, (float) z2).color(r, g, bl, a);
    }

    private static void drawBox(BufferBuilder b, Matrix4f m, Box box, float r, float g, float bl, float a) {
        double x1 = box.minX, y1 = box.minY, z1 = box.minZ;
        double x2 = box.maxX, y2 = box.maxY, z2 = box.maxZ;
        // нижний контур
        line(b, m, x1,y1,z1, x2,y1,z1, r,g,bl,a); line(b, m, x2,y1,z1, x2,y1,z2, r,g,bl,a);
        line(b, m, x2,y1,z2, x1,y1,z2, r,g,bl,a); line(b, m, x1,y1,z2, x1,y1,z1, r,g,bl,a);
        // верхний контур
        line(b, m, x1,y2,z1, x2,y2,z1, r,g,bl,a); line(b, m, x2,y2,z1, x2,y2,z2, r,g,bl,a);
        line(b, m, x2,y2,z2, x1,y2,z2, r,g,bl,a); line(b, m, x1,y2,z2, x1,y2,z1, r,g,bl,a);
        // вертикали
        line(b, m, x1,y1,z1, x1,y2,z1, r,g,bl,a); line(b, m, x2,y1,z1, x2,y2,z1, r,g,bl,a);
        line(b, m, x2,y1,z2, x2,y2,z2, r,g,bl,a); line(b, m, x1,y1,z2, x1,y2,z2, r,g,bl,a);
    }
}
