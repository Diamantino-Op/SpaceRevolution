package com.diamantino.spacerevolution.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class AsteroidCountdownData extends PersistentState {
    public int countdownTicks;

    public AsteroidCountdownData() {
        this.countdownTicks = 0;
    }

    public static AsteroidCountdownData loadNbt(NbtCompound nbt) {
        AsteroidCountdownData data = new AsteroidCountdownData();

        data.countdownTicks = nbt.getInt("countdown");

        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("countdown", countdownTicks);

        return nbt;
    }
}
