package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.mixin.WorldRenderMixin;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public class ModCloudRenderer {
    private final Identifier cloudTexture;

    public ModCloudRenderer(Identifier cloudTexture) {
        this.cloudTexture = cloudTexture;
    }

    public void render(ClientWorld level, int ticks, float tickDelta, MatrixStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix) {

        MinecraftClient minecraft = MinecraftClient.getInstance();
        WorldRenderMixin renderer = (WorldRenderMixin) minecraft.worldRenderer;

        float f = level.getDimensionEffects().getCloudsHeight();
        if (!Float.isNaN(f)) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            double e = ((float) ticks + tickDelta) * 0.03f;
            double i = (cameraX + e) / 12.0;
            double j = f - (float) cameraY + 0.33f;
            double k = cameraZ / 12.0 + 0.33f;
            i -= Math.floor(i / 2048.0) * 2048;
            k -= Math.floor(k / 2048.0) * 2048;
            float l = (float) (i - Math.floor(i));
            float m = (float) (j / 4.0 - Math.floor(j / 4.0)) * 4.0f;
            float n = (float) (k - Math.floor(k));
            Vec3d colour = level.getCloudsColor(tickDelta);
            int o = (int) Math.floor(i);
            int p = (int) Math.floor(j / 4.0);
            int q = (int) Math.floor(k);
            if (o != renderer.getLastCloudsBlockX() || p != renderer.getLastCloudsBlockY() || q != renderer.getLastCloudsBlockZ() || minecraft.options.getCloudRenderModeValue() != renderer.getLastCloudRenderMode() || renderer.getLastCloudsColor().distanceTo(colour) > 2.0E-4) {
                renderer.setLastCloudsBlockX(o);
                renderer.setLastCloudsBlockY(p);
                renderer.setLastCloudsBlockZ(q);
                renderer.setLastCloudsColor(colour);
                renderer.setLastCloudRenderMode(minecraft.options.getCloudRenderModeValue());
                renderer.setCloudsDirty(true);
            }

            if (renderer.getCloudsDirty()) {
                renderer.setCloudsDirty(false);
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                if (renderer.getCloudsBuffer() != null) {
                    renderer.getCloudsBuffer().close();
                }
                renderer.setCloudsBuffer(new VertexBuffer());
                BufferBuilder.BuiltBuffer builtBuffer = renderer.invokeRenderClouds(bufferBuilder, i, j, k, colour);
                renderer.getCloudsBuffer().bind();
                renderer.getCloudsBuffer().upload(builtBuffer);
                VertexBuffer.unbind();
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
            RenderSystem.setShaderTexture(0, cloudTexture);
            BackgroundRenderer.setFogBlack();
            poseStack.push();
            poseStack.scale(12.0f, 1.0f, 12.0f);
            poseStack.translate(-l, m, -n);
            if (renderer.getCloudsBuffer() != null) {
                renderer.getCloudsBuffer().bind();
                int r = renderer.getLastCloudRenderMode().equals(CloudRenderMode.FANCY) ? 0 : 1;

                for (int s = r; s < 2; ++s) {
                    if (s == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }

                    ShaderProgram shaderProgram = RenderSystem.getShader();
                    renderer.getCloudsBuffer().draw(poseStack.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                }

                VertexBuffer.unbind();
            }

            poseStack.pop();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}