package com.diamantino.spacerevolution.utils;

import com.diamantino.spacerevolution.data.PlanetData;
import com.diamantino.spacerevolution.initialization.ModReferences;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class OxygenUtils {

    // Contains every pos in all dimensions with oxygen.
    public static final Map<Pair<RegistryKey<World>, BlockPos>, Set<BlockPos>> OXYGEN_LOCATIONS = new HashMap<>();

    /**
     * Checks if a level has oxygen, regardless of position.
     */
    public static boolean levelHasOxygen(World level) {
        if (!PlanetData.isOxygenated(level.getRegistryKey())) {
            // Ensure all non-Ad Astra dimensions have oxygen by default
            return !ModUtils.isSpacelevel(level);
        }
        return true;
    }

    /**
     * Checks if an entity has oxygen.
     */
    public static boolean entityHasOxygen(World level, LivingEntity entity) {
        return posHasOxygen(level, new BlockPos(entity.getEyePos()));
    }

    /**
     * Checks if there is oxygen in a specific block in a specific dimension.
     */
    @SuppressWarnings("deprecation")
    public static boolean posHasOxygen(World level, BlockPos pos) {

        if (!level.isChunkLoaded(pos)) {
            return true;
        }

        if (levelHasOxygen(level)) {
            return true;
        }

        return inDistributorBubble(level, pos);
    }

    public static boolean inDistributorBubble(World level, BlockPos pos) {
        for (Map.Entry<Pair<RegistryKey<World>, BlockPos>, Set<BlockPos>> entry : OXYGEN_LOCATIONS.entrySet()) {
            if (level.getRegistryKey().equals(entry.getKey().getLeft())) {
                if (entry.getValue().contains(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the amount of blocks that an oxygen distributor is distributing.
     *
     * @param level  The level to check for oxygen in
     * @param source The oxygen distributor position
     * @return The amount of blocks that an oxygen distributor is distributing oxygen to
     */
    public static int getOxygenBlocksCount(World level, BlockPos source) {
        return OXYGEN_LOCATIONS.getOrDefault(getOxygenSource(level, source), Set.of()).size();
    }

    public static void setEntry(World level, BlockPos source, Set<BlockPos> entries) {
        // Get all the entries that have changed. If they are have been removed, deoxygenate their pos.
        if (!level.isClient()) {
            if (OXYGEN_LOCATIONS.containsKey(getOxygenSource(level, source))) {
                Set<BlockPos> changedPositions = new HashSet<>(OXYGEN_LOCATIONS.get(getOxygenSource(level, source)));
                if (!changedPositions.isEmpty()) {
                    changedPositions.removeAll(entries);
                    deoxygenizeBlocks((ServerWorld) level, changedPositions, source);
                }
            }
        }
        OXYGEN_LOCATIONS.put(getOxygenSource(level, source), entries);
    }

    public static void removeEntry(World level, BlockPos source) {
        OxygenUtils.setEntry(level, source, Set.of());
    }

    /**
     * Removes the oxygen from a set of blocks. For example, turns water into ice or air, converts torches into extinguished torches, puts out flames, kills plants etc.
     */
    public static void deoxygenizeBlocks(ServerWorld level, Set<BlockPos> entries, BlockPos source) {
        try {
            if (entries == null) {
                return;
            }
            if (entries.isEmpty()) {
                return;
            }

            if (levelHasOxygen(level)) {
                OXYGEN_LOCATIONS.remove(getOxygenSource(level, source));
                return;
            }

            for (BlockPos pos : new HashSet<>(entries)) {

                BlockState state = level.getBlockState(pos);

                OXYGEN_LOCATIONS.get(getOxygenSource(level, source)).remove(pos);
                if (posHasOxygen(level, pos)) {
                    continue;
                }

                if (state.isAir()) {
                    continue;
                }

                Block block = state.getBlock();
                /*if (block instanceof WallTorchBlock && !block.equals(Blocks.SOUL_WALL_TORCH)) {
                    level.setBlockState(pos, ModBlocks.WALL_EXTINGUISHED_TORCH.get().defaultBlockState().setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING)));
                    continue;
                }

                if (block instanceof TorchBlock && !block.equals(Blocks.SOUL_TORCH) && !block.equals(Blocks.SOUL_WALL_TORCH)) {
                    level.setBlockState(pos, ModBlocks.EXTINGUISHED_TORCH.get().defaultBlockState());
                    continue;
                }*/

                if (block instanceof CandleCakeBlock) {
                    level.setBlockState(pos, block.getDefaultState().with(CandleCakeBlock.LIT, false));
                    continue;
                }

                if (block instanceof CandleBlock) {
                    level.setBlockState(pos, block.getDefaultState().with(CandleBlock.CANDLES, state.get(CandleBlock.CANDLES)).with(CandleBlock.LIT, false));
                    continue;
                }

                if (block instanceof FireBlock) {
                    level.removeBlock(pos, false);
                    continue;
                }

                if (block instanceof CampfireBlock) {
                    level.setBlockState(pos, state.with(CampfireBlock.LIT, false).with(CampfireBlock.FACING, state.get(CampfireBlock.FACING)));
                    continue;
                }

                if (block instanceof GrassBlock) {
                    level.setBlockState(pos, Blocks.DIRT.getDefaultState());
                    continue;
                }

                if (block instanceof SweetBerryBushBlock || block instanceof CactusBlock || block instanceof VineBlock) {
                    level.removeBlock(pos, true);
                    continue;
                }

                if (block instanceof FarmlandBlock) {
                    level.setBlockState(pos, state.with(FarmlandBlock.MOISTURE, 0));
                    continue;
                }

                /*if (state.getFluidState().isIn(FluidTags.WATER)) {
                    if (!block.equals(ModBlocks.CRYO_FUEL_BLOCK.get())) {
                        if (ModUtils.getWorldTemperature(level) < 0) {
                            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                        } else {
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    } else if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.WATERLOGGED, false));
                    }
                }*/
            }
        } catch (UnsupportedOperationException e) {
            ModReferences.logger.error("Error deoxygenizing blocks");
            e.printStackTrace();
        }
    }

    private static Pair<RegistryKey<World>, BlockPos> getOxygenSource(World level, BlockPos source) {
        return Pair.of(level.getRegistryKey(), source);
    }
}