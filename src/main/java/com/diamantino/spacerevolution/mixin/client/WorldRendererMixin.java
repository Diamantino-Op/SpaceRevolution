package com.diamantino.spacerevolution.mixin.client;

import com.diamantino.spacerevolution.client.dimension.ModDimensionEffects;
import com.diamantino.spacerevolution.client.dimension.renderer.DimensionEffects;
import com.diamantino.spacerevolution.initialization.ModParticleTypes;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.utils.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int ticks;

    @Shadow
    private int rainSoundCounter;

    @Inject(at = @At("HEAD"), method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", cancellable = true)
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (client.world != null) {
            if (ModDimensionEffects.dimensionEffects.containsKey(client.world.getRegistryKey())) {
                DimensionEffects effects = ModDimensionEffects.dimensionEffects.get(client.world.getRegistryKey());

                if (effects.shouldRenderSky()) {
                    if (effects.renderSky(client.world, ticks, tickDelta, matrices, camera, projectionMatrix, bl, runnable))
                        ci.cancel();
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", cancellable = true)
    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci) {
        if (client.world != null) {
            if (ModDimensionEffects.dimensionEffects.containsKey(client.world.getRegistryKey())) {
                DimensionEffects effects = ModDimensionEffects.dimensionEffects.get(client.world.getRegistryKey());

                if (effects.shouldRenderClouds()) {
                    if (ModDimensionEffects.dimensionEffects.get(client.world.getRegistryKey()).renderClouds(client.world, ticks, tickDelta, matrices, d, e, f, projectionMatrix))
                        ci.cancel();
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", cancellable = true)
    private void renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (client.world != null) {
            if (ModDimensionEffects.dimensionEffects.containsKey(client.world.getRegistryKey())) {
                DimensionEffects effects = ModDimensionEffects.dimensionEffects.get(client.world.getRegistryKey());

                if (effects.shouldRenderSnowAndRain()) {
                    if (ModDimensionEffects.dimensionEffects.get(client.world.getRegistryKey()).renderSnowAndRain(client.world, ticks, tickDelta, manager, cameraX, cameraY, cameraZ))
                        ci.cancel();
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V")
    public void tickRainSplashing(Camera camera, CallbackInfo ci) {
        if (client.world != null) {
            if (ModUtils.isPlanet(client.world)) {
                float f = client.world.getRainGradient(1.0F) / (MinecraftClient.isFancyGraphicsOrBetter() ? 1.0F : 2.0F);
                if (!(f <= 0.0F)) {
                    Random randomGenerator = Random.create((long) this.ticks * 312987231L);
                    World levelView = client.world;
                    BlockPos blockPos = new BlockPos(camera.getPos());
                    BlockPos blockPos2 = null;
                    int i = (int) (100.0F * f * f) / (client.options.getParticles().getValue() == ParticlesMode.DECREASED ? 2 : 1);

                    for (int j = 0; j < i; ++j) {
                        int k = randomGenerator.nextInt(21) - 10;
                        int l = randomGenerator.nextInt(21) - 10;
                        BlockPos blockPos3 = levelView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.offset(Direction.fromVector(k, 0, l)));
                        Biome biome = levelView.getBiome(blockPos3).value();
                        if (blockPos3.getY() > levelView.getBottomY() && blockPos3.getY() <= blockPos.getY() + 10 && blockPos3.getY() >= blockPos.getY() - 10 && biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(blockPos3)) {
                            blockPos2 = blockPos3.down();
                            if (client.options.getParticles().getValue() == ParticlesMode.MINIMAL) {
                                break;
                            }

                            double d = randomGenerator.nextDouble();
                            double e = randomGenerator.nextDouble();
                            BlockState blockState = levelView.getBlockState(blockPos2);
                            FluidState fluidState = levelView.getFluidState(blockPos2);
                            VoxelShape voxelShape = blockState.getCollisionShape(levelView, blockPos2);
                            double g = voxelShape.getEndingCoord(Direction.Axis.Y, d, e);
                            double h = fluidState.getHeight(levelView, blockPos2);
                            double m = Math.max(g, h);
                            DefaultParticleType particleEffect = !fluidState.isIn(FluidTags.LAVA) && !blockState.isOf(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockState) ? ParticleTypes.SMOKE : ModParticleTypes.planetRain;
                            client.world.addParticle(particleEffect, (double) blockPos2.getX() + d, (double) blockPos2.getY() + m, (double) blockPos2.getZ() + e, 0.0, 0.0, 0.0);
                        }
                    }

                    if (blockPos2 != null && randomGenerator.nextInt(3) < rainSoundCounter++) {
                        rainSoundCounter = 0;
                        if (blockPos2.getY() > blockPos.getY() + 1 && levelView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > Math.floor((float) blockPos.getY())) {
                            client.world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
                        } else {
                            client.world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "processWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V", cancellable = true)
    public void processWorldEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (eventId == WorldEvents.TRAVEL_THROUGH_PORTAL) {
            ClientPlayerEntity player = client.player;

            // Don't player the portal sound if the player teleported to the new level.
            /*if (player != null && ((int) player.getPos().y) == VehiclesConfig.RocketConfig.atmosphereLeave) {
                ci.cancel();
            }*/
        }
    }
}
