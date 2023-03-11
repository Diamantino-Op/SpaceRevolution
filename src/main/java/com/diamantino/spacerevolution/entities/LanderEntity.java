package com.diamantino.spacerevolution.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LanderEntity extends BlockyVehicleEntity {
    public LanderEntity(EntityType<?> type, World world) {
        super(type, world, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
