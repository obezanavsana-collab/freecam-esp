package com.example.fcesp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.Input;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class FreeCameraEntity extends ClientPlayerEntity {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FreeCameraEntity() {
        super(mc, mc.world, mc.player.getNetworkHandler(),
                mc.player.getStatHandler(), mc.player.getRecipeBook(), false, false);
        setId(-1000); // уникальный ID, чтобы не конфликтовать с игроком
        copyPositionAndRotation(mc.player);
        setInvisible(true);      // не рисуем модель
        setNoGravity(true);
    }

    // Чанки подгружаются от игрока, а не от камеры - без этого за пределами
    // render distance от камеры будут дырки в мире
    @Override
    public net.minecraft.util.math.ChunkPos getChunkPos() {
        return mc.player != null ? mc.player.getChunkPos() : super.getChunkPos();
    }

    @Override
    public void tick() {
        if (mc.player == null || mc.world == null || !mc.player.isAlive()) {
            Freecam.disable();
            return;
        }

        // Поворот мыши применяем к камере
        setYaw(mc.player.getYaw());
        setPitch(mc.player.getPitch());

        prevX = getX(); prevY = getY(); prevZ = getZ();
        prevYaw = getYaw(); prevPitch = getPitch();

        Input input = mc.player.input;
        double speed = input.sprinting ? 1.8 : 0.8;

        Vec3d dir = Vec3d.ZERO;
        dir = dir.add(Vec3d.fromPolar(0, getYaw()).multiply(input.movementForward));
        dir = dir.add(Vec3d.fromPolar(0, getYaw() + 90).multiply(input.movementSideways));
        if (input.jumping) dir = dir.add(0, 1, 0);
        if (input.sneaking) dir = dir.add(0, -1, 0);
        if (dir.lengthSquared() > 0) dir = dir.normalize();

        setVelocity(dir.multiply(speed));
        move(MovementType.SELF, getVelocity());
        fallDistance = 0;
    }
}
