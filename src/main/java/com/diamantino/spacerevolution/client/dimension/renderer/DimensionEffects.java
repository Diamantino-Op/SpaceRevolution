package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import com.diamantino.spacerevolution.utils.ColourUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class DimensionEffects extends net.minecraft.client.render.DimensionEffects implements DimensionRenderer {
    private final PlanetSkyRenderer renderer;
    private final ModSkyRenderer skyRenderer;

    public DimensionEffects(PlanetSkyRenderer renderer) {
        super(192, true, net.minecraft.client.render.DimensionEffects.SkyType.NORMAL, false, false);
        this.renderer = renderer;
        this.skyRenderer = new ModSkyRenderer(renderer);
    }

    @Override
    public float[] getFogColorOverride(float skyAngle, float tickDelta) {
        PlanetSkyRenderer.DimensionEffectType type = renderer.effects().type();
        if (type == PlanetSkyRenderer.DimensionEffectType.FOGGY_REVERSED || type == PlanetSkyRenderer.DimensionEffectType.NONE) {
            return null;
        }
        return super.getFogColorOverride(skyAngle, tickDelta);
    }

    @Override
    public boolean renderClouds(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix) {
        return switch (renderer.cloudEffects()) {
            case NONE -> true;
            case VANILLA -> false;
            case VENUS -> {
                VenusCloudRenderer.render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
        };
    }

    @Override
    public boolean shouldRenderClouds() {
        return renderer.cloudEffects() != PlanetSkyRenderer.CloudEffects.VANILLA;
    }

    @Override
    public boolean renderSky(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean foggy, Runnable setupFog) {
        setupFog.run();
        skyRenderer.render(level, ticks, tickDelta, poseStack, camera, projectionMatrix, foggy);
        return true;
    }

    @Override
    public boolean shouldRenderSky() {
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientWorld level, int ticks, float tickDelta, TextureManager manager, double cameraX, double cameraY, double cameraZ) {
        return switch (renderer.weatherEffects()) {
            case NONE -> true;
            case VANILLA -> false;
            case VENUS -> {
                ModWeatherRenderer.render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
        };
    }

    @Override
    public boolean shouldRenderSnowAndRain() {
        return renderer.weatherEffects() != PlanetSkyRenderer.WeatherEffects.VANILLA;
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        if (renderer.effects().type().equals(PlanetSkyRenderer.DimensionEffectType.COLORED_HORIZON)) {
            return ColourUtils.toVector(renderer.effects().colour());
        }
        return color.multiply(sunHeight * 0.94f + 0.06f, sunHeight * 0.94f + 0.06f, sunHeight * 0.91f + 0.09f);
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return renderer.effects().type().isFoggy();
    }
}