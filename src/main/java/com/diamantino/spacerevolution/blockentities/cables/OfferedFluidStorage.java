package com.diamantino.spacerevolution.blockentities.cables;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

/**
 * {@link SingleVariantStorage} adjacent to an energy cable, with some additional info.
 */
public class OfferedFluidStorage {
    final FluidPipeBlockEntity sourceCable;
    final Direction direction;
    final SingleVariantStorage<FluidVariant> storage;

    OfferedFluidStorage(FluidPipeBlockEntity sourceCable, Direction direction, SingleVariantStorage<FluidVariant> storage) {
        this.sourceCable = sourceCable;
        this.direction = direction;
        this.storage = storage;
    }

    void afterTransfer() {
        // Block insertions from this side.
        sourceCable.blockedSides |= 1 << direction.ordinal();
    }
}
