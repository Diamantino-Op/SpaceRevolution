package com.diamantino.spacerevolution.client.dimension.renderer;

import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.joml.Matrix4f;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public interface DimensionRenderer {
    boolean renderClouds(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix);

    boolean renderSky(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean foggy, Runnable setupFog);

    boolean renderSnowAndRain(ClientWorld level, int ticks, float tickDelta, TextureManager manager, double cameraX, double cameraY, double cameraZ);

    boolean shouldRenderClouds();

    boolean shouldRenderSky();

    boolean shouldRenderSnowAndRain();
}