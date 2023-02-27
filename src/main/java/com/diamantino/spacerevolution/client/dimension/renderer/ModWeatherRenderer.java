package com.diamantino.spacerevolution.client.dimension.renderer;

import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.mixin.WorldRenderMixin;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class ModWeatherRenderer {

    private static final Identifier VENUS_RAIN_TEXTURE = new Identifier(ModReferences.modId, "textures/sky/venus/rain.png");

    public static void render(ClientWorld level, int ticks, float tickDelta, TextureManager manager, double cameraX, double cameraY, double cameraZ) {

        MinecraftClient minecraft = MinecraftClient.getInstance();
        WorldRenderer renderer = minecraft.worldRenderer;

        assert minecraft.world != null;
        float h = minecraft.world.getRainGradient(tickDelta);
        if (!(h <= 0.0f)) {
            int i = (int) Math.floor(cameraX);
            int j = (int) Math.floor(cameraY);
            int k = (int) Math.floor(cameraZ);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int l = 5;
            if (MinecraftClient.isFancyGraphicsOrBetter()) {
                l = 10;
            }

            RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
            int m = -1;
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int o = k - l; o <= k + l; ++o) {
                for (int p = i - l; p <= i + l; ++p) {
                    int q = (o - k + 16) * 32 + p - i + 16;
                    double r = (double) Objects.requireNonNull(renderer.getWeatherFramebuffer()).textureHeight * 0.5;
                    double s = (double) Objects.requireNonNull(renderer.getWeatherFramebuffer()).textureWidth * 0.5;
                    mutable.set(p, cameraY, o);
                    Biome biome = level.getBiome(mutable).value();
                    if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
                        int t = level.getTopY(Heightmap.Type.MOTION_BLOCKING, p, o);
                        int u = j - l;
                        int v = j + l;
                        if (u < t) {
                            u = t;
                        }

                        if (v < t) {
                            v = t;
                        }

                        int w = Math.max(t, j);

                        if (u != v) {
                            Random random = new Random((long) p * p * 3121 + p * 45238971L ^ (long) o * o * 418711 + o * 13761L);
                            mutable.set(p, u, o);
                            float y;
                            float ac;
                            if (biome.getTemperature() > 0.5f) {
                                if (m != 0) {

                                    m = 0;
                                    RenderSystem.setShaderTexture(0, VENUS_RAIN_TEXTURE);
                                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                                }

                                int x = ((WorldRenderMixin) renderer).getTicks() + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 31;
                                y = -((float) x + tickDelta) / 32.0f * (3.0f + random.nextFloat());
                                double z = (double) p + 0.5 - cameraX;
                                double aa = (double) o + 0.5 - cameraZ;
                                float ab = (float) Math.sqrt(z * z + aa * aa) / (float) l;
                                ac = ((1.0f - ab * ab) * 0.5f + 0.5f) * h;
                                mutable.set(p, w, o);
                                int ad = WorldRenderer.getLightmapCoordinates(level, mutable);
                                bufferBuilder.vertex((double) p - cameraX - r + 0.5, (double) v - cameraY, (double) o - cameraZ - s + 0.5).texture(0.0f, (float) u * 0.25f + y).color(1.0f, 1.0f, 1.0f, ac).light(ad).next();
                                bufferBuilder.vertex((double) p - cameraX + r + 0.5, (double) v - cameraY, (double) o - cameraZ + s + 0.5).texture(1.0f, (float) u * 0.25f + y).color(1.0f, 1.0f, 1.0f, ac).light(ad).next();
                                bufferBuilder.vertex((double) p - cameraX + r + 0.5, (double) u - cameraY, (double) o - cameraZ + s + 0.5).texture(1.0f, (float) v * 0.25f + y).color(1.0f, 1.0f, 1.0f, ac).light(ad).next();
                                bufferBuilder.vertex((double) p - cameraX - r + 0.5, (double) u - cameraY, (double) o - cameraZ - s + 0.5).texture(0.0f, (float) v * 0.25f + y).color(1.0f, 1.0f, 1.0f, ac).light(ad).next();
                            }
                        }
                    }
                }
            }

            if (m >= 0) {
                tessellator.draw();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}