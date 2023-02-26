package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import com.diamantino.spacerevolution.utils.Color;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.GeneratorOptions;
import org.joml.Matrix4f;
import org.joml.Vector3f;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public class SkyUtil {

    // Scales the planet as you fall closer to it.
    public static float getScale() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        float distance = (float) (-3000.0f + minecraft.player.getY() * 4.5f);
        float scale = 100 * (0.2f - distance / 10000.0f);
        return Math.max(scale, 0.5f);
    }

    public static void preRender(ClientWorld level, WorldRenderer levelRenderer, Camera camera, Matrix4f projectionMatrix, BufferBuilder bufferBuilder, PlanetSkyRenderer.SunsetColour colourType, int sunsetAngle, MatrixStack poseStack, float tickDelta) {

        // Render colours.
        RenderSystem.disableTexture();
        Vec3d vec3d = level.getSkyColor(camera.getPos(), tickDelta);
        float f = (float) vec3d.getX();
        float g = (float) vec3d.getY();
        float h = (float) vec3d.getZ();
        BackgroundRenderer.setFogBlack();
        RenderSystem.depthMask(false);

        RenderSystem.setShaderColor(f, g, h, 1.0f);
        levelRenderer.renderSky(poseStack, projectionMatrix, tickDelta, camera, false, null);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        renderColouring(colourType, bufferBuilder, poseStack, level, tickDelta, level.getTimeOfDay(), sunsetAngle);
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void postRender(GameRenderer renderer, ClientWorld level, float tickDelta) {

        Vec3d vec3d = level.getSkyColor(renderer.getCamera().getPos(), tickDelta);
        float f = (float) vec3d.getX();
        float g = (float) vec3d.getY();
        float h = (float) vec3d.getZ();

        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);

        if (!level.getDimensionEffects().isAlternateSkyColor()) {
            RenderSystem.setShaderColor(f * 0.2f + 0.04f, g * 0.2f + 0.04f, h * 0.6f + 0.1f, 1.0f);
        } else {
            RenderSystem.setShaderColor(f, g, h, 1.0f);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
    }

    public static boolean isSubmerged(Camera camera) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType.equals(CameraSubmersionType.POWDER_SNOW) || cameraSubmersionType.equals(CameraSubmersionType.LAVA)) {
            return true;
        }
        if (camera.getFocusedEntity() instanceof LivingEntity livingEntity) {
            return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
        }
        return false;
    }

    public static void startRendering(MatrixStack poseStack, Vector3f rotation) {

        poseStack.push();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Rotation
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.y()));
        poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.z()));
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.x()));
    }

    private static void endRendering(MatrixStack poseStack) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        poseStack.pop();
    }

    // For rendering textures in the sky
    public static void render(MatrixStack poseStack, BufferBuilder bufferBuilder, Identifier texture, Color colour, Vector3f rotation, float scale, boolean blending) {

        startRendering(poseStack, rotation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        RenderSystem.setShaderColor(colour.getIntRed() / 255f, colour.getIntGreen() / 255f, colour.getIntBlue() / 255f, 1f);

        if (blending) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        Matrix4f positionMatrix = poseStack.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, texture);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(positionMatrix, -scale, 100.0f, -scale).texture(1.0f, 0.0f).color(colour.getIntRed(), colour.getIntGreen(), colour.getIntBlue(), 255).next();
        bufferBuilder.vertex(positionMatrix, scale, 100.0f, -scale).texture(0.0f, 0.0f).color(colour.getIntRed(), colour.getIntGreen(), colour.getIntBlue(), 255).next();
        bufferBuilder.vertex(positionMatrix, scale, 100.0f, scale).texture(0.0f, 1.0f).color(colour.getIntRed(), colour.getIntGreen(), colour.getIntBlue(), 255).next();
        bufferBuilder.vertex(positionMatrix, -scale, 100.0f, scale).texture(1.0f, 1.0f).color(colour.getIntRed(), colour.getIntGreen(), colour.getIntBlue(), 255).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        endRendering(poseStack);
    }

    public static BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer, int stars, boolean colouredStars) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        StarInformation info = StarInformation.STAR_CACHE.apply(GeneratorOptions.getRandomSeed(), stars);
        for (int i = 0; i < stars; ++i) {
            Vector3f vec3f = info.getParam1(i);
            float d = vec3f.x();
            float e = vec3f.y();
            float f = vec3f.z();
            float g = info.getMultiplier(i);
            float h = d * d + e * e + f * f;
            if (h >= 1 || h <= 0.01f) continue;
            h = (float) (1f / Math.sqrt(h));
            d *= h;
            e *= h;
            f *= h;
            float j = d * 100.0f;
            float k = e * 100.0f;
            float l = f * 100.0f;
            float m = (float) Math.atan2(d, f);
            float n = (float) Math.sin(m);
            float o = (float) Math.cos(m);
            float p = (float) Math.atan2(Math.sqrt(d * d + f * f), e);
            float q = (float) Math.sin(p);
            float r = (float) Math.cos(p);
            float s = info.getRandomPi(i);
            float t = (float) Math.sin(s);
            float u = (float) Math.cos(s);

            for (int v = 0; v < 4; ++v) {
                float x = ((v & 2) - 1) * g;
                float y = ((v + 1 & 2) - 1) * g;
                float aa = x * u - y * t;
                float ac = y * u + x * t;
                float ae = aa * -r;

                Color colour = info.getColour(i, v, colouredStars);
                buffer.vertex(j + ae * n - ac * o, k + aa * q, l + ac * n + ae * o).color(colour.getIntRed(), colour.getIntGreen(), colour.getIntBlue(), colour.getIntAlpha()).next();
            }
        }
        return buffer.end();
    }

    // Custom blue sunset and sunrise
    public static float[] getMarsColour(float skyAngle) {
        float[] colours = new float[4];

        float cosine = (float) (Math.cos(skyAngle * ((float) Math.PI * 2f)) - 0.0f);
        if (cosine >= -0.4f && cosine <= 0.4f) {
            float c = (cosine + 0.0f) / 0.4f * 0.5f + 0.5f;
            float sine = (float) (1.0f - (1.0f - Math.sin(c * (float) Math.PI)) * 0.99f);
            sine *= sine;
            colours[0] = c * 0.3f;
            colours[1] = c * c * 0.6f + 0.55f;
            colours[2] = c * c * 0.0f + 0.8f;
            colours[3] = sine;
            return colours;
        } else {
            return null;
        }
    }

    public static void renderColouring(PlanetSkyRenderer.SunsetColour type, BufferBuilder bufferBuilder, MatrixStack poseStack, ClientWorld level, float tickDelta, float timeOfDay, int sunsetAngle) {

        float[] fogColours = switch (type) {
            case VANILLA -> level.getDimensionEffects().getFogColorOverride(timeOfDay, tickDelta);
            case MARS -> getMarsColour(timeOfDay);
        };
        if (fogColours != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            poseStack.push();
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            float sine = Math.sin(level.getSkyAngle(tickDelta)) < 0f ? 180f : 0f;
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sine));
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));

            Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(fogColours[0], fogColours[1], fogColours[2], fogColours[3]).next();

            for (int i = 0; i <= 16; ++i) {
                float o = (float) (i * (Math.PI * 2) / 16.0f);
                float cosine = (float) Math.cos(o);
                bufferBuilder.vertex(matrix4f, (float) (Math.sin(o) * 120.0f), cosine * 120.0f, -cosine * 40.0f * fogColours[3]).color(fogColours[0], fogColours[1], fogColours[2], 0.0f).next();
            }

            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            poseStack.pop();
        }
    }
}