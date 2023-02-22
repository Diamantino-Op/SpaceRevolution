package com.diamantino.spacerevolution.variants;

public enum EnergyTier {
    BASIC(1024, 1024),
    INTERMEDIATE(32768, 32768),
    ADVANCED(1048576, 1048576),
    ELITE(33554432, 33554432),
    ULTIMATE(1073741824, 1073741824),
    QUANTUM(Long.MAX_VALUE, Long.MAX_VALUE);

    private final long maxInput;
    private final long maxOutput;

    private EnergyTier(long maxInput, long maxOutput) {
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    public long getMaxInput() {
        return this.maxInput;
    }

    public long getMaxOutput() {
        return this.maxOutput;
    }

    public static EnergyTier getTier(long power) {
        EnergyTier[] var2 = values();

        for (EnergyTier tier : var2) {
            if (tier.getMaxInput() >= power) {
                return tier;
            }
        }

        return QUANTUM;
    }
}