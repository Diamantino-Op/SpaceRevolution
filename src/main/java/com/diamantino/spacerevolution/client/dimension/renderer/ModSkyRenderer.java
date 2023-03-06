package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public class ModSkyRenderer {

    private final PlanetSkyRenderer.StarsRenderer starsRenderer;
    private final PlanetSkyRenderer.SunsetColour sunsetColour;
    private final List<PlanetSkyRenderer.SkyObject> skyObjects;
    private final int horizonAngle;
    private final boolean shouldRenderWhileRaining;

    private VertexBuffer starsBuffer;
    private int starsCount;

    public ModSkyRenderer(PlanetSkyRenderer skyRenderer) {
        this.starsRenderer = skyRenderer.starsRenderer();
        this.sunsetColour = skyRenderer.sunsetColour();
        this.skyObjects = skyRenderer.skyObjects();
        this.horizonAngle = skyRenderer.horizonAngle();
        this.shouldRenderWhileRaining = !skyRenderer.weatherEffects().equals(PlanetSkyRenderer.WeatherEffects.NONE);
    }

    public void render(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean foggy) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        MinecraftClient minecraft = MinecraftClient.getInstance();

        if (shouldRenderWhileRaining && level.isRaining()) {
            return;
        }
        // Cancel rendering if the player is in fog, i.e. in lava or powdered snow
        if (SkyUtil.isSubmerged(camera)) {
            return;
        }

        SkyUtil.preRender(level, minecraft.worldRenderer, camera, projectionMatrix, bufferBuilder, this.sunsetColour, horizonAngle, poseStack, tickDelta);

        // Stars
        if (this.starsRenderer.fastStars() > 0) {
            int stars = (!minecraft.options.getGraphicsMode().getValue().equals(GraphicsMode.FAST) ? this.starsRenderer.fancyStars() : this.starsRenderer.fastStars());
            starsBuffer = renderStars(level, poseStack, tickDelta, bufferBuilder, stars, this.starsRenderer, projectionMatrix);
        }

        // Render all sky objects
        for (PlanetSkyRenderer.SkyObject skyObject : this.skyObjects) {

            float scale = skyObject.scale();
            Vector3f rotation = skyObject.rotation();
            switch (skyObject.renderType()) {
                case STATIC -> {} // Do not modify the scale or rotation
                case DYNAMIC -> rotation = new Vector3f(level.getTimeOfDay() * 360.0f + rotation.x(), rotation.y(), rotation.z());
                case SCALING -> scale *= SkyUtil.getScale();
                case DEBUG -> rotation = new Vector3f(60, 0, 0); // Test things without restarting Minecraft
            }
            SkyUtil.render(poseStack, bufferBuilder, skyObject.texture(), skyObject.colour(), rotation, scale, skyObject.blending());
        }

        SkyUtil.postRender(minecraft.gameRenderer, level, tickDelta);
    }

    private VertexBuffer renderStars(ClientWorld level, MatrixStack poseStack, float tickDelta, BufferBuilder bufferBuilder, int stars, PlanetSkyRenderer.StarsRenderer starsRenderer, Matrix4f projectionMatrix) {

        SkyUtil.startRendering(poseStack, new Vector3f(-30.0f, 0.0f, level.getTimeOfDay() * 360.0f));
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        createStarBuffer(bufferBuilder, stars);

        if (!starsRenderer.daylightVisible()) {
            float rot = level.getStarBrightness(tickDelta);
            RenderSystem.setShaderColor(rot, rot, rot, rot);
        } else {
            RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 0.8f);
        }

        BackgroundRenderer.clearFog();
        starsBuffer.bind();
        starsBuffer.draw(poseStack.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionColorProgram());
        VertexBuffer.unbind();

        poseStack.pop();
        return starsBuffer;
    }

    private void createStarBuffer(BufferBuilder bufferBuilder, int stars) {
        if (starsBuffer != null) {
            if (starsCount == stars) {
                return;
            }
            starsBuffer.close();
        }

        starsBuffer = new VertexBuffer();
        starsCount = stars;
        BufferBuilder.BuiltBuffer renderedBuffer = SkyUtil.renderStars(bufferBuilder, stars, starsRenderer.colouredStars());
        starsBuffer.bind();
        starsBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }
}