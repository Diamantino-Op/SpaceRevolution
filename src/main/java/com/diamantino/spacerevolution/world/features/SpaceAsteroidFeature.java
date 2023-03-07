package com.diamantino.spacerevolution.world.features;

import com.diamantino.spacerevolution.initialization.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class SpaceAsteroidFeature extends Feature<DefaultFeatureConfig> {
    private final SimpleBlockStateProvider ASTEROID_STONE;

    private static final DataPool.Builder<BlockState> pool = new DataPool.Builder<>();

    private final WeightedBlockStateProvider ASTEROID_ORE;

    public SpaceAsteroidFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);

        pool.add(Blocks.COAL_ORE.getDefaultState(), 5);
        pool.add(Blocks.IRON_ORE.getDefaultState(), 5);
        pool.add(Blocks.GOLD_ORE.getDefaultState(), 3);
        pool.add(Blocks.REDSTONE_ORE.getDefaultState(), 3);
        pool.add(Blocks.LAPIS_ORE.getDefaultState(), 2);
        pool.add(Blocks.DIAMOND_ORE.getDefaultState(), 1);
        pool.add(Blocks.EMERALD_ORE.getDefaultState(), 1);
        pool.add(ModBlocks.resourcefulAsteroidBlock.getDefaultState(), 10);

        ASTEROID_STONE = SimpleBlockStateProvider.of(ModBlocks.asteroidBlock.getDefaultState());
        ASTEROID_ORE = new WeightedBlockStateProvider(pool);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos origin = context.getOrigin();
        ChunkPos chunkPos = new ChunkPos(origin);
        ChunkRandom random = new ChunkRandom(Random.create(chunkPos.x ^ chunkPos.z));

        BlockPos.Mutable pos = new BlockPos.Mutable();

        int xRadius = random.nextBetween(0, 32);
        int yRadius = random.nextBetween(0, 32);
        int zRadius = random.nextBetween(0, 32);

        for(int x = -xRadius; x <= xRadius; x++) {
            for(int y = -yRadius; y <= yRadius; y++) {
                for(int z = -zRadius; z <= zRadius; z++) {
                    double distance = Math.pow(x / (double)xRadius, 2) + Math.pow(y / (double)yRadius, 2) + Math.pow(z / (double)zRadius, 2);
                    if(distance <= 1) {
                        pos.set(x + origin.getX(), y + origin.getY(), z + origin.getZ());

                        BlockState state = ASTEROID_STONE.get(random, pos);
                        if (random.nextDouble() < 0.05) {
                            state = ASTEROID_ORE.get(random, pos);
                        }
                        context.getWorld().setBlockState(pos, state, 3);
                    }
                }
            }
        }

        return true;
    }
}