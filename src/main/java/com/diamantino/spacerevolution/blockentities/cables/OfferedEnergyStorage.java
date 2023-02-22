package com.diamantino.spacerevolution.blockentities.cables;

import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

/**
 * {@link EnergyStorage} adjacent to an energy cable, with some additional info.
 */
// CREDIT: https://github.com/TechReborn/TechReborn
// UNDER MIT LICENSE: https://github.com/TechReborn/TechReborn/blob/1.19/LICENSE.md
class OfferedEnergyStorage {
    final ElectricCableBlockEntity sourceCable;
    final Direction direction;
    final EnergyStorage storage;

    OfferedEnergyStorage(ElectricCableBlockEntity sourceCable, Direction direction, EnergyStorage storage) {
        this.sourceCable = sourceCable;
        this.direction = direction;
        this.storage = storage;
    }

    void afterTransfer() {
        // Block insertions from this side.
        sourceCable.blockedSides |= 1 << direction.ordinal();
    }
}