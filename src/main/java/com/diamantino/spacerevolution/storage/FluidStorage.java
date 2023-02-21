package com.diamantino.spacerevolution.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;

public class FluidStorage {
    private final List<SingleVariantStorage<FluidVariant>> fluidStorages = new ArrayList<>();
    private final List<FluidTankDimensions> fluidTankDimensions = new ArrayList<>();

    public void addStorage(SingleVariantStorage<FluidVariant> storage, int x, int y, int width, int height) {
        fluidStorages.add(storage);
        fluidTankDimensions.add(new FluidTankDimensions(x, y, width, height));
    }

    public SingleVariantStorage<FluidVariant> getStorage(int index) {
        return fluidStorages.get(index);
    }

    public FluidTankDimensions getStorageDimensions(int index) {
        return fluidTankDimensions.get(index);
    }

    public int getSize() {
        return fluidStorages.size();
    }

    public void saveStorages(NbtCompound nbt, String name) {
        NbtCompound storagesTag = new NbtCompound();

        int i = 0;
        for (SingleVariantStorage<FluidVariant> fluidStorage : fluidStorages) {
            NbtCompound temp = new NbtCompound();

            fluidStorage.writeNbt(temp);

            storagesTag.put("fluidStorage." + i, temp);

            i++;
        }

        nbt.put(name + ".fluidStorages", storagesTag);
    }

    public void loadStorages(NbtCompound nbt, String name) {
        NbtCompound storagesTag = nbt.getCompound(name + ".fluidStorages");

        int i = 0;
        for (SingleVariantStorage<FluidVariant> fluidStorage : fluidStorages) {
            NbtCompound temp = storagesTag.getCompound("fluidStorage." + i);

            if (temp != null) {
                fluidStorage.variant = FluidVariant.fromNbt(temp.getCompound("variant"));
                fluidStorage.amount = temp.getLong("amount");
            }

            i++;
        }
    }

    public class FluidTankDimensions {
        public int x;
        public int y;
        public int width;
        public int height;

        public FluidTankDimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
