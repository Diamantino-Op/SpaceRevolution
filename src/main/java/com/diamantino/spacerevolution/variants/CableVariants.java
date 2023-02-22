package com.diamantino.spacerevolution.variants;

import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;

import java.util.Locale;

public class CableVariants {
    public enum Electric {
        COPPER(1024, 10.0, true, EnergyTier.BASIC),
        INSULATED_COPPER(1024, 10.0, false, EnergyTier.BASIC),
        GOLD(32768, 10.0, true, EnergyTier.INTERMEDIATE),
        INSULATED_GOLD(32768, 10.0, false, EnergyTier.INTERMEDIATE),
        SILVER(1048576, 10.0, true, EnergyTier.ADVANCED),
        INSULATED_SILVER(1048576, 10.0, false, EnergyTier.ADVANCED),
        GLASS_FIBER(33554432, 10.0, false, EnergyTier.ELITE),
        PLASMA(1073741824, 10, false, EnergyTier.ULTIMATE),
        QUANTUM(Long.MAX_VALUE, 10.0, false, EnergyTier.QUANTUM);

        public final String name;
        public final ElectricCableBlock block;
        public final long transferRate;
        public final long defaultTransferRate;
        public final double cableThickness;
        public final boolean canKill;
        public final boolean defaultCanKill;
        public final EnergyTier tier;

        Electric(long transferRate, double cableThickness, boolean canKill, EnergyTier tier) {
            this.name = this.toString().toLowerCase(Locale.ROOT);
            this.transferRate = transferRate;
            this.defaultTransferRate = transferRate;
            this.cableThickness = cableThickness / 2.0 / 16.0;
            this.canKill = canKill;
            this.defaultCanKill = canKill;
            this.tier = tier;
            this.block = new ElectricCableBlock(this);
        }
    }
}
