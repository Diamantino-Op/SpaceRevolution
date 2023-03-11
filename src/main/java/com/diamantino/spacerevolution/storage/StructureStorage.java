package com.diamantino.spacerevolution.storage;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureStorage {
    private final Map<BlockPos, BlockState> blocks = Maps.newHashMap();
    private final Map<BlockPos, NbtCompound> blockEntityNbts = Maps.newHashMap();

    private final World world;

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    public StructureStorage(World world, BlockPos startPos, BlockPos endPos) {
        this.world = world;

        this.sizeX = Math.abs(endPos.getX() - startPos.getX());
        this.sizeY = Math.abs(endPos.getY() - startPos.getY());
        this.sizeZ = Math.abs(endPos.getZ() - startPos.getZ());
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public BlockState getBlockState(BlockPos pos) {
        return blocks.getOrDefault(pos, Blocks.AIR.getDefaultState());
    }

    public NbtCompound getBlockEntityNbt(BlockPos pos) {
        return blockEntityNbts.getOrDefault(pos, new NbtCompound());
    }

    public void saveData(NbtCompound nbt) {
        NbtCompound blocksNbt = new NbtCompound();
        AtomicInteger blocksIndex = new AtomicInteger();

        blocks.forEach((pos, state) -> {
            NbtCompound tag = new NbtCompound();
            tag.put("state", NbtHelper.fromBlockState(state));
            tag.put("pos", NbtHelper.fromBlockPos(pos));

            blocksNbt.put(String.valueOf(blocksIndex.get()), tag);

            blocksIndex.getAndIncrement();
        });

        blocksNbt.putInt("mapSize", (blocksIndex.get() - 1));

        nbt.put("blocks", blocksNbt);

        NbtCompound blockEntitiesNbt = new NbtCompound();
        AtomicInteger blockEntitiesIndex = new AtomicInteger();

        blockEntityNbts.forEach((pos, entityNbt) -> {
            NbtCompound tag = new NbtCompound();
            tag.put("nbt", entityNbt);
            tag.put("pos", NbtHelper.fromBlockPos(pos));

            blockEntitiesNbt.put(String.valueOf(blockEntitiesIndex.get()), tag);

            blockEntitiesIndex.getAndIncrement();
        });

        blockEntitiesNbt.putInt("mapSize", (blockEntitiesIndex.get() - 1));

        nbt.put("blockEntities", blockEntitiesNbt);
    }

    public void loadData(NbtCompound nbt) {
        blocks.clear();
        blockEntityNbts.clear();

        NbtCompound blocksNbt = nbt.getCompound("blocks");

        int blocksMapSize = blocksNbt.getInt("mapSize");

        for (int i = 0; i < blocksMapSize; i++) {
            NbtCompound tag = blocksNbt.getCompound(String.valueOf(i));

            blocks.put(NbtHelper.toBlockPos(tag.getCompound("pos")), NbtHelper.toBlockState(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), tag.getCompound("state")));
        }

        NbtCompound blockEntitiesNbt = nbt.getCompound("blocks");

        int blockEntitiesMapSize = blockEntitiesNbt.getInt("mapSize");

        for (int i = 0; i < blockEntitiesMapSize; i++) {
            NbtCompound tag = blockEntitiesNbt.getCompound(String.valueOf(i));

            blockEntityNbts.put(NbtHelper.toBlockPos(tag.getCompound("pos")), tag.getCompound("nbt"));
        }
    }

    public void loadBlocks(BlockPos startPos, BlockPos endPos) {
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                for (int z = 0; z <= sizeZ; z++) {
                    BlockPos pos = new BlockPos(Math.min(startPos.getX(), endPos.getX()) + x, Math.min(startPos.getY(), endPos.getY()) + y, Math.min(startPos.getZ(), endPos.getZ()) + z);
                    BlockPos localPos = new BlockPos(x, y, z);

                    if (blocks.containsKey(localPos)) {
                        world.setBlockState(pos, blocks.get(localPos));

                        if (blockEntityNbts.containsKey(localPos)) {
                            BlockEntity entity = world.getBlockEntity(pos);

                            if (entity != null)
                                entity.readNbt(blockEntityNbts.get(localPos));
                        }
                    }
                }
            }
        }
    }

    public void saveBlocks(BlockPos startPos, BlockPos endPos) {
        for (int x = 0; x <= sizeX; x++) {
            for (int y = 0; y <= sizeY; y++) {
                for (int z = 0; z <= sizeZ; z++) {
                    BlockPos pos = new BlockPos(Math.min(startPos.getX(), endPos.getX()) + x, Math.min(startPos.getY(), endPos.getY()) + y, Math.min(startPos.getZ(), endPos.getZ()) + z);
                    BlockPos localPos = new BlockPos(x, y, z);

                    BlockState state = world.getBlockState(pos);
                    BlockEntity entity = world.getBlockEntity(pos);

                    if (state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof FluidBlock)) {
                        blocks.put(localPos, state);

                        if (entity != null) {
                            blockEntityNbts.put(localPos, entity.createNbtWithId());
                        }

                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}
