package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.utils.ColourUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class DimensionEffects extends net.minecraft.client.render.DimensionEffects {
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

    public boolean renderClouds(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix) {
        return switch (renderer.cloudEffects()) {
            case NONE -> true;
            case VANILLA -> false;
            case VENUS -> {
                new ModCloudRenderer(new Identifier(ModReferences.modId, "textures/sky/venus/clouds.png")).render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
            case JUPITER -> {
                new ModCloudRenderer(new Identifier(ModReferences.modId, "textures/sky/jupiter/clouds.png")).render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
            case SATURN -> {
                new ModCloudRenderer(new Identifier(ModReferences.modId, "textures/sky/saturn/clouds.png")).render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
            case URANUS -> {
                new ModCloudRenderer(new Identifier(ModReferences.modId, "textures/sky/uranus/clouds.png")).render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
            case NEPTUNE -> {
                new ModCloudRenderer(new Identifier(ModReferences.modId, "textures/sky/neptune/clouds.png")).render(level, ticks, tickDelta, poseStack, cameraX, cameraY, cameraZ, projectionMatrix);
                yield true;
            }
        };
    }

    public boolean shouldRenderClouds() {
        return renderer.cloudEffects() != PlanetSkyRenderer.CloudEffects.VANILLA;
    }

    public boolean renderSky(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean foggy, Runnable setupFog) {
        if (setupFog != null)
            setupFog.run();

        skyRenderer.render(level, ticks, tickDelta, poseStack, camera, projectionMatrix, foggy);
        
        return true;
    }

    public boolean shouldRenderSky() {
        return true;
    }

    public boolean renderSnowAndRain(ClientWorld level, int ticks, float tickDelta, LightmapTextureManager manager, double cameraX, double cameraY, double cameraZ) {
        return switch (renderer.weatherEffects()) {
            case NONE -> true;
            case VANILLA -> false;
            case VENUS -> {
                new ModWeatherRenderer(new Identifier(ModReferences.modId, "textures/sky/venus/rain.png")).render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
            case JUPITER -> {
                new ModWeatherRenderer(new Identifier(ModReferences.modId, "textures/sky/jupiter/rain.png")).render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
            case SATURN -> {
                new ModWeatherRenderer(new Identifier(ModReferences.modId, "textures/sky/saturn/rain.png")).render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
            case URANUS -> {
                new ModWeatherRenderer(new Identifier(ModReferences.modId, "textures/sky/uranus/rain.png")).render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
            case NEPTUNE -> {
                new ModWeatherRenderer(new Identifier(ModReferences.modId, "textures/sky/neptune/rain.png")).render(level, ticks, tickDelta, manager, cameraX, cameraY, cameraZ);
                yield true;
            }
        };
    }

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