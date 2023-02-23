package com.diamantino.spacerevolution.variants;

import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;
import com.diamantino.spacerevolution.blocks.cables.FluidPipeBlock;

import java.util.Locale;

public class CableVariants {
    public enum Electric {
        COPPER(1024, 10.0, true, Tier.BASIC),
        INSULATED_COPPER(1024, 10.0, false, Tier.BASIC),
        GOLD(32768, 10.0, true, Tier.INTERMEDIATE),
        INSULATED_GOLD(32768, 10.0, false, Tier.INTERMEDIATE),
        SILVER(1048576, 10.0, true, Tier.ADVANCED),
        INSULATED_SILVER(1048576, 10.0, false, Tier.ADVANCED),
        GLASS_FIBER(33554432, 10.0, false, Tier.ELITE),
        PLASMA(1073741824, 10, false, Tier.ULTIMATE),
        QUANTUM(Long.MAX_VALUE, 10.0, false, Tier.QUANTUM);

        public final String name;
        public final ElectricCableBlock block;
        public final long transferRate;
        public final long defaultTransferRate;
        public final double cableThickness;
        public final boolean canKill;
        public final boolean defaultCanKill;
        public final Tier tier;

        Electric(long transferRate, double cableThickness, boolean canKill, Tier tier) {
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

    public enum Fluid {
        BASIC(1024, 10.0, Tier.BASIC),
        INTERMEDIATE(32768, 10.0, Tier.INTERMEDIATE),
        ADVANCED(1048576, 10.0, Tier.ADVANCED),
        ELITE(33554432, 10.0, Tier.ELITE),
        ULTIMATE(1073741824, 10.0, Tier.ULTIMATE),
        QUANTUM(Long.MAX_VALUE, 10.0, Tier.QUANTUM);

        public final String name;
        public final FluidPipeBlock block;
        public final long transferRate;
        public final long defaultTransferRate;
        public final double cableThickness;
        public final Tier tier;

        Fluid(long transferRate, double cableThickness, Tier tier) {
            this.name = this.toString().toLowerCase(Locale.ROOT);
            this.transferRate = transferRate;
            this.defaultTransferRate = transferRate;
            this.cableThickness = cableThickness / 2.0 / 16.0;
            this.tier = tier;
            this.block = new FluidPipeBlock(this);
        }
    }
}
