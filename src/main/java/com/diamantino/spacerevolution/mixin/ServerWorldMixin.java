package com.diamantino.spacerevolution.mixin;

import com.diamantino.spacerevolution.data.AsteroidCountdownData;
import com.diamantino.spacerevolution.entities.AsteroidEntity;
import com.diamantino.spacerevolution.initialization.ModReferences;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract ServerWorld toServerWorld();

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    @Shadow public abstract long getSeed();

    @Shadow public abstract PointOfInterestStorage getPointOfInterestStorage();

    @Shadow public abstract boolean spawnEntity(Entity entity);

    private AsteroidCountdownData asteroidData;

    private boolean hasSaidAsteroidWaring = false;

    @Inject(at = @At("HEAD"), method = "tick(Ljava/util/function/BooleanSupplier;)V")
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (this.server.getOverworld() == this.toServerWorld()) {
            if (asteroidData.countdownTicks <= 6000 && !hasSaidAsteroidWaring) {
                List<ServerPlayerEntity> players = this.server.getOverworld().getPlayers();

                for (ServerPlayerEntity player : players) {
                    //TODO: Add translation
                    player.sendMessageToClient(Text.translatable("warnings.spacerevolution.asteroid_incoming"), false);
                }

                hasSaidAsteroidWaring = true;
            }

            if (asteroidData.countdownTicks > 0) {
                asteroidData.countdownTicks--;

                asteroidData.markDirty();
            } else {
                spawnAsteroid();

                asteroidData.countdownTicks = resetAsteroidCooldown();

                asteroidData.markDirty();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;Z)V")
    public void init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {
        if (dimensionOptions.dimensionTypeEntry().matchesKey(DimensionTypes.OVERWORLD)) {
            asteroidData = getPersistentStateManager().getOrCreate(AsteroidCountdownData::loadNbt, AsteroidCountdownData::new, "asteroid_countdown");

            if (asteroidData.countdownTicks == 0) {
                asteroidData.countdownTicks = resetAsteroidCooldown();

                asteroidData.markDirty();
            }
        }
    }

    private void spawnAsteroid() {
        List<ServerPlayerEntity> players = this.server.getOverworld().getPlayers();

        if (!players.isEmpty()) {
            Random rand = Random.create(getSeed());

            ServerPlayerEntity unfortunatePlayer = players.get(rand.nextBetween(0, players.size() - 1));

            List<PointOfInterest> POIs = getPointOfInterestStorage().getInCircle(registryEntry -> true, unfortunatePlayer.getBlockPos(), 128, PointOfInterestStorage.OccupationStatus.ANY).toList();

            if (!POIs.isEmpty()) {
                PointOfInterest poi = POIs.get(rand.nextBetween(0, POIs.size() - 1));

                AsteroidEntity entity = new AsteroidEntity(this.toServerWorld(), new Vec3d(poi.getPos().getX(), poi.getPos().getY(), poi.getPos().getZ()), new Vec3d(poi.getPos().getX() + 256, this.server.getOverworld().getTopY(), poi.getPos().getZ() + 256));

                this.spawnEntity(entity);
            }
        }
    }

    private int resetAsteroidCooldown() {
        Random rand = Random.create(getSeed());

        //TODO: Maybe add this to config
        return rand.nextBetween(12000, 36000);
    }
}
