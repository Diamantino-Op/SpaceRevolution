package com.diamantino.spacerevolution.utils;

import com.diamantino.spacerevolution.config.ModCommonConfigs;
import com.diamantino.spacerevolution.data.Planet;
import com.diamantino.spacerevolution.data.PlanetData;
import com.diamantino.spacerevolution.entities.BlockyVehicleEntity;
import com.diamantino.spacerevolution.entities.LanderEntity;
import com.diamantino.spacerevolution.entities.RocketEntity;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class ModUtils {
    public static final RegistryKey<World> MOON_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(ModReferences.modId, "moon"));
    public static final RegistryKey<World> MARS_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(ModReferences.modId, "mars"));
    public static final RegistryKey<World> VENUS_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(ModReferences.modId, "venus"));
    public static final RegistryKey<World> MERCURY_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(ModReferences.modId, "mercury"));

    public static final float VANILLA_GRAVITY = 9.806f;
    public static final float ORBIT_TEMPERATURE = -270.0f;

    //TODO: Fix

    /**
     * Teleports an entity to a different dimension. If the entity is a player in a rocket, the player will teleport with a lander. If the entity is raw food, the food will be cooked.
     *
     * @param targetWorld The level to the entity teleport to
     * @param entity      The entity to teleport
     * @see #teleportPlayer(RegistryKey, ServerPlayerEntity)
     */
    public static void teleportToLevel(RegistryKey<World> targetWorld, Entity entity) {
        if (entity.getWorld() instanceof ServerWorld oldWorld) {
            ServerWorld level = oldWorld.getServer().getWorld(targetWorld);
            if (level == null) return;
            Set<Entity> entitiesToTeleport = new LinkedHashSet<>();

            Vec3d targetPos = new Vec3d(entity.getX(), ModCommonConfigs.atmosphereLeaveLevel.getValue(), entity.getZ());

            if (entity instanceof ServerPlayerEntity player) {
                if (player.getVehicle() instanceof RocketEntity rocket) {
                    rocket.removeAllPassengers();
                    player.sendMessageToClient(Text.translatable("message." + ModReferences.modId + ".hold_space"), false);
                    entity = createLander(rocket, level, targetPos);
                    rocket.discard();
                    entitiesToTeleport.add(entity);
                    entitiesToTeleport.add(player);

                } else if (!(player.getVehicle() != null && player.getVehicle().getPassengerList().size() > 0)) {
                    entitiesToTeleport.add(entity);
                }
            } else {
                entitiesToTeleport.add(entity);
            }

            if (entity instanceof ItemEntity itemEntity) {
                cookFood(itemEntity);
            }

            entitiesToTeleport.addAll(entity.getPassengerList());

            for (Entity entityToTeleport : entitiesToTeleport) {
                if (entityToTeleport instanceof ServerPlayerEntity) {
                    ChunkPos chunkPos = new ChunkPos(new BlockPos(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                    level.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, entityToTeleport.getId());
                    break;
                }
            }

            LinkedList<Entity> teleportedEntities = new LinkedList<>();
            for (Entity entityToTeleport : entitiesToTeleport) {
                PortalInfo target = new PortalInfo(targetPos, entityToTeleport.getVelocity(), entityToTeleport.getYaw(), entityToTeleport.getPitch());
                teleportedEntities.add(PlatformUtils.teleportToDimension(entityToTeleport, level, target));
            }

            Entity first = teleportedEntities.poll();

            // Move the lander to the closest land
            if (first instanceof LanderEntity) {
                Vec3d nearestLand = LandFinder.findNearestLand(first.getWorld(), new Vec3d(first.getX(), ModCommonConfigs.atmosphereLeaveLevel.getValue(), first.getZ()), 70);
                first.refreshPositionAndAngles(nearestLand.getX(), nearestLand.getY(), nearestLand.getZ(), first.getYaw(), first.getPitch());
            }

            for (Entity teleportedEntity : teleportedEntities) {
                if (first instanceof LanderEntity) {
                    Vec3d nearestLand = LandFinder.findNearestLand(teleportedEntity.getWorld(), new Vec3d(teleportedEntity.getX(), ModCommonConfigs.atmosphereLeaveLevel.getValue(), teleportedEntity.getZ()), 70);
                    teleportedEntity.refreshPositionAndAngles(nearestLand.getX(), nearestLand.getY(), nearestLand.getZ(), teleportedEntity.getYaw(), teleportedEntity.getPitch());
                }
                if (teleportedEntity != null) {
                    teleportedEntity.startRiding(first, true);
                }
            }
        }
    }

    public static void teleportPlayer(RegistryKey<World> targetWorld, ServerPlayerEntity player) {
        /*ServerWorld level = player.getServer().getWorld(targetWorld);
        Vec3d targetPos = new Vec3d(player.getPos().getX(), VehiclesConfig.RocketConfig.atmosphereLeave, player.getPos().getZ());
        ChunkPos chunkPos = new ChunkPos(new BlockPos(targetPos.x(), targetPos.y(), targetPos.z()));
        level.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
        PortalInfo target = new PortalInfo(targetPos, player.getDeltaMovement(), player.getYRot(), player.getXRot());
        PlatformUtils.teleportToDimension(player, level, target);*/
    }

    /**
     * Spawns a lander in a target level and position.
     *
     * @param rocket      The rocket to create a lander from
     * @param targetWorld The level to spawn the lander in
     * @param target      The position to spawn the lander at
     * @return A spawned lander entity at the same position as the rocket and with the same inventory
     */
    public static LanderEntity createLander(RocketEntity rocket, ServerWorld targetWorld, Vec3d target) {
        LanderEntity lander = new LanderEntity(ModEntityTypes.LANDER.get(), targetWorld);
        lander.setPos(target.getX(), target.getY(), target.getZ());

        /*for (int i = 0; i < rocket.getInventorySize(); i++) {
            lander.getInventory().setItem(i, rocket.getInventory().getItem(i));
        }
        ItemStackHolder stack = new ItemStackHolder(rocket.getDropStack());
        ((VehicleItem) stack.getStack().getItem()).insert(stack, rocket.getTankHolder());
        lander.getInventory().setItem(10, stack.getStack());*/

        targetWorld.spawnEntity(lander);

        return lander;
    }

    /**
     * Gets the cooked variant of a raw food, if it exists, and then spawns the item entity. The cooked variant is obtained by using a smoking recipe, and then obtaining the result of that recipe.
     *
     * @param itemEntity The item to try to convert into cooked food
     */
    public static void cookFood(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getStack();
        ItemStack foodOutput = ItemStack.EMPTY;

        Inventory inv = new SimpleInventory(1);
        inv.setStack(0, stack);

        for (SmokingRecipe recipe : itemEntity.getWorld().getRecipeManager().getAllMatches(RecipeType.SMOKING, inv, itemEntity.getWorld())) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(stack)) {
                    foodOutput = recipe.getOutput();
                }
            }
        }

        if (!foodOutput.isEmpty()) {
            itemEntity.setStack(new ItemStack(foodOutput.getItem(), stack.getCount()));
        }
    }

    /**
     * Gets the level's orbit dimension. The orbit dimension is where the planet's space station spawns and where the lander drops.
     *
     * @return The level's orbit dimension, or the overlevel if no orbit is defined
     */
    public static RegistryKey<World> getPlanetOrbit(World level) {
        return PlanetData.getPlanetFromOrbit(level.getRegistryKey()).map(Planet::level).orElse(World.OVERWORLD);
    }

    public static float getEntityGravity(Entity entity) {
        return getPlanetGravity(entity.getWorld());
    }

    /**
     * Gets the gravity of the level, in ratio to earth gravity. So a gravity of 1.0 is equivalent to earth gravity, while 0.5 would be half of earth's gravity and 2.0 would be twice the earth's gravity.
     *
     * @return The gravity of the level or earth gravity if the level does not have a defined gravity
     */
    public static float getPlanetGravity(World level) {
        // Do not affect gravity for non-Ad Astra dimensions
        if (!ModUtils.isSpacelevel(level)) {
            return 1.0f;
        }
        //TODO: Fix
        /*if (isOrbitlevel(level)) {
            return AdAstraConfig.orbitGravity / VANILLA_GRAVITY;
        }*/
        return PlanetData.getPlanetFromLevel(level.getRegistryKey()).map(Planet::gravity).orElse(VANILLA_GRAVITY) / VANILLA_GRAVITY;
    }

    public static boolean planetHasAtmosphere(World level) {
        return PlanetData.getPlanetFromLevel(level.getRegistryKey()).map(Planet::hasAtmosphere).orElse(false);
    }

    /**
     * Gets the temperature of the level in celsius.
     *
     * @return The temperature of the level, or 20° for dimensions without a defined temperature
     */
    public static float getWorldTemperature(World level) {
        if (isOrbitlevel(level)) {
            return ORBIT_TEMPERATURE;
        }
        return PlanetData.getPlanetFromLevel(level.getRegistryKey()).map(Planet::temperature).orElse(20.0f);
    }

    /**
     * Checks if the level is either a planet or an orbit level.
     */
    public static boolean isSpacelevel(World level) {
        return isPlanet(level) || isOrbitlevel(level);
    }

    /**
     * Check if the level is labeled as a planet dimension.
     */
    public static boolean isPlanet(World level) {
        if (World.OVERWORLD.equals(level.getRegistryKey())) {
            return false;
        }
        return PlanetData.isPlanetLevel(level);
    }

    /**
     * Checks if the level is labeled as an orbit dimension.
     */
    public static boolean isOrbitlevel(World level) {
        return PlanetData.isOrbitLevel(level.getRegistryKey());
    }

    /**
     * Spawns a server-side particle that renders regardless of the distance away from the player. This is important as normal particles are only rendered at up to 32 blocks away.
     */
    public static <T extends ParticleOptions> void spawnForcedParticles(ServerWorld level, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        for (ServerPlayerEntity player : level.getPlayers()) {
            level.spawnParticles(player, particle, true, x, y, z, count, deltaX, deltaY, deltaZ, speed);
        }
    }

    /**
     * Rotates the vehicle yaw without causing any stuttering effect or visual glitches.
     *
     * @param vehicle The vehicle to apply the rotation
     * @param newYaw  The new yaw to apply to the vehicle
     */
    public static void rotateVehicleYaw(BlockyVehicleEntity vehicle, float newYaw) {
        vehicle.setYaw(newYaw);
        vehicle.setBodyYaw(newYaw);
        vehicle.prevYaw = newYaw;
    }

    public static boolean checkTag(Entity entity, TagKey<EntityType<?>> tag) {
        return entity.getType().isIn(tag);
    }

    public static boolean checkTag(ItemStack stack, TagKey<Item> tag) {
        return stack.isIn(tag);
    }

    /*public static boolean armourIsFreezeResistant(LivingEntity entity) {
        return StreamSupport.stream(entity.getArmorItems().spliterator(), false).allMatch(s -> s.isIn(ModTags.FREEZE_RESISTANT));
    }

    public static boolean armourIsHeatResistant(LivingEntity entity) {
        return StreamSupport.stream(entity.getArmorItems().spliterator(), false).allMatch(s -> s.isIn(ModTags.HEAT_RESISTANT));
    }

    public static boolean armourIsOxygenated(LivingEntity entity) {
        return StreamSupport.stream(entity.getArmorItems().spliterator(), false).allMatch(s -> s.isIn(ModTags.OXYGENATED_ARMOR));
    }*/

    public static long getSolarEnergy(World level) {
        if (isOrbitlevel(level)) {
            return PlanetData.getPlanetFromOrbit(level.getRegistryKey()).map(Planet::orbitSolarPower).orElse(15L);
        } else if (isPlanet(level)) {
            return PlanetData.getPlanetFromLevel(level.getRegistryKey()).map(Planet::solarPower).orElse(15L);
        } else {
            return 15L;
        }
    }

    public static <T extends Enum<T>> Codec<T> createEnumCodec(Class<T> enumClass) {
        return Codec.STRING.xmap(s -> Enum.valueOf(enumClass, s.toUpperCase(Locale.ROOT)), Enum::name);
    }
}