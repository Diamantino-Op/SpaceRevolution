package com.diamantino.spacerevolution.blockentities;

import com.diamantino.spacerevolution.initialization.ModMessages;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.inventory.ImplementedInventory;
import com.diamantino.spacerevolution.recipes.BaseRecipe;
import com.diamantino.spacerevolution.storage.FluidStorage;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseMachineBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    final DefaultedList<ItemStack> inventory;

    public final SimpleSidedEnergyContainer energyStorage;

    public final FluidStorage fluidStorages;

    protected final PropertyDelegate propertyDelegate;
    int progress;
    int maxProgress;
    long baseEnergyUsage;
    long baseLubricantUsage;

    final String name;

    List<Integer> inputSlots = new ArrayList<>();
    static int outputSlot;

    static BaseRecipe.BaseType<?> recipeType;

    int energySlot;
    int fluidSlot;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, String name, int inventorySize, int maxProgress, int outSlot, BaseRecipe.BaseType<?> recType, long energyCapacity, long maxInsert, long maxExtract, boolean acceptEnergy, boolean provideEnergy, long baseEnergyUsage, List<Direction> energyPorts, int energySlot, long lubricantCapacity, long baseLubricantUsage, int fluidSlot) {
        super(type, pos, state);

        this.energyStorage = new SimpleSidedEnergyContainer() {
            @Override
            public long getCapacity() {
                return energyCapacity;
            }

            @Override
            public long getMaxInsert(@Nullable Direction side) {
                return (acceptEnergy && energyPorts.contains(side)) ? maxInsert : 0;
            }

            @Override
            public long getMaxExtract(@Nullable Direction side) {
                return (provideEnergy && energyPorts.contains(side)) ? maxExtract : 0;
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                if (!Objects.requireNonNull(getWorld()).isClient()) {
                    sendEnergyPacket();
                }
            }
        };

        this.energySlot = energySlot;
        this.fluidSlot = fluidSlot;

        fluidStorages = new FluidStorage();

        fluidStorages.addStorage(new SingleVariantStorage<>() {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant) {
                return lubricantCapacity;
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return false;
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return true;
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                assert world != null;
                if(!world.isClient()) {
                    sendFluidPacket();
                }
            }
        }, 12, 13, 16, 64);

        this.baseEnergyUsage = baseEnergyUsage;
        this.baseLubricantUsage = baseLubricantUsage;

        this.name = name;

        outputSlot = outSlot;
        recipeType = recType;

        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        this.progress = 0;
        this.maxProgress = maxProgress;

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BaseMachineBlockEntity.this.progress;
                    case 1 -> BaseMachineBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BaseMachineBlockEntity.this.progress = value;
                    case 1 -> BaseMachineBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void markDirty() {
        assert world != null;
        if(!world.isClient()) {
            PacketByteBuf data = PacketByteBufs.create();

            data.writeInt(inventory.size());

            for (ItemStack itemStack : inventory) {
                data.writeItemStack(itemStack);
            }

            data.writeBlockPos(getPos());

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.inventorySyncPacket, data);
            }
        }

        super.markDirty();
    }

    void sendEnergyPacket() {
        PacketByteBuf data = PacketByteBufs.create();

        data.writeLong(energyStorage.amount);
        data.writeBlockPos(getPos());

        assert world != null;
        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.energySyncPacket, data);
        }
    }

    void sendFluidPacket() {
        PacketByteBuf data = PacketByteBufs.create();

        data.writeInt(fluidStorages.getSize());

        for (int i = 0; i < fluidStorages.getSize(); i++) {
            fluidStorages.getStorage(i).variant.toPacket(data);
            data.writeLong(fluidStorages.getStorage(i).amount);
            data.writeBlockPos(getPos());
        }

        assert world != null;
        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
            ServerPlayNetworking.send(player, ModMessages.fluidSyncPacket, data);
        }
    }

    public void setEnergyLevel(long energyLevel) {
        this.energyStorage.amount = energyLevel;
    }

    public void setFluidLevel(int storage, FluidVariant fluidVariant, long fluidLevel) {
        this.fluidStorages.getStorage(storage).variant = fluidVariant;
        this.fluidStorages.getStorage(storage).amount = fluidLevel;
    }

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    //TODO: Modify with tiers
    long getEnergyUsage() {
        return baseEnergyUsage;
    }

    long getLubricantUsage() {
        return baseLubricantUsage;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("machine." + ModReferences.modId + "." + name + ".name");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put(name + ".inventory", Inventories.writeNbt(new NbtCompound(), inventory));
        nbt.putInt(name + ".progress", progress);
        nbt.putLong(name + ".energy", energyStorage.amount);

        fluidStorages.saveStorages(nbt, name);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound(name + ".inventory"), inventory);
        progress = nbt.getInt(name + ".progress");
        energyStorage.amount = nbt.getLong(name + ".energy");

        fluidStorages.loadStorages(nbt, name);
    }

    void resetProgress() {
        this.progress = 0;
    }

    public void useEnergy(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.updateSnapshots(transaction);

            energyStorage.amount -= Math.min(energyStorage.amount, amount);

            transaction.commit();
        }
    }

    public void addEnergy(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {

            if ((energyStorage.amount + amount) <= energyStorage.getCapacity()) {
                energyStorage.updateSnapshots(transaction);

                energyStorage.amount += amount;
            }

            transaction.commit();
        }
    }

    public void setEnergy(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            energyStorage.updateSnapshots(transaction);

            energyStorage.amount = Math.min(energyStorage.getCapacity(), amount);

            transaction.commit();
        }
    }

    public void useFluid(int storageNum, FluidVariant variant, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            SingleVariantStorage<FluidVariant> storage = fluidStorages.getStorage(storageNum);

            if (variant.equals(storage.variant)) {
                storage.updateSnapshots(transaction);

                storage.amount -= Math.min(storage.amount, amount);

                if (storage.amount == 0) {
                    storage.variant = FluidVariant.blank();
                }
            }

            transaction.commit();
        }
    }

    public boolean addFluid(int storageNum, FluidVariant variant, long amount) {
        boolean hasAdded = false;

        try (Transaction transaction = Transaction.openOuter()) {
            SingleVariantStorage<FluidVariant> storage = fluidStorages.getStorage(storageNum);

            if (variant.equals(storage.variant) || storage.variant.isBlank()) {
                if ((storage.amount + amount) <= storage.getCapacity()) {
                    storage.updateSnapshots(transaction);

                    if (storage.variant.isBlank())
                        storage.variant = variant;

                    storage.amount += amount;

                    hasAdded = true;
                }
            }

            transaction.commit();
        }

        return hasAdded;
    }

    public void setFluid(int storageNum, FluidVariant variant, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            SingleVariantStorage<FluidVariant> storage = fluidStorages.getStorage(storageNum);

            storage.updateSnapshots(transaction);

            storage.variant = variant;
            storage.amount = amount;

            transaction.commit();
        }
    }

    static boolean hasEnoughEnergy(BaseMachineBlockEntity entity) {
        return entity.energyStorage.amount >= entity.getEnergyUsage();
    }

    static boolean hasEnoughLubricant(BaseMachineBlockEntity entity) {
        return entity.fluidStorages.getStorage(0).amount >= entity.getLubricantUsage();
    }

    //TODO: Finish this
    static boolean hasEnergyItemInSlot(BaseMachineBlockEntity entity) {
        return false;
    }

    static boolean hasFluidStorageInSlot(BaseMachineBlockEntity entity) {
        return entity.getStack(entity.fluidSlot).getItem() == Items.WATER_BUCKET;
    }

    static boolean hasRecipe(BaseMachineBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(entity.size());

        for (int i = 0; i < entity.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        Optional<? extends BaseRecipe> match = Objects.requireNonNull(entity.getWorld()).getRecipeManager().getFirstMatch(recipeType, inventory, entity.getWorld());

        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory, match.get().getOutput().getCount()) && canInsertItemIntoOutputSlot(inventory, match.get().getOutput().getItem());
    }

    public ItemStack getRenderStack() {
        SimpleInventory inventory = new SimpleInventory(size());

        for (int i = 0; i < size(); i++) {
            inventory.setStack(i, getStack(i));
        }

        Optional<? extends BaseRecipe> match = Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(recipeType, inventory, getWorld());

        if (match.isPresent())
            return match.get().getOutput().copy();

        return getStack(outputSlot);
    }

    static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, Item output) {
        return inventory.getStack(outputSlot).getItem() == output.asItem() || inventory.getStack(outputSlot).isEmpty();
    }

    static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory, int count) {
        return inventory.getStack(outputSlot).getMaxCount() >= inventory.getStack(outputSlot).getCount() + count;
    }

    void craftItem(BaseMachineBlockEntity entity) {
        SimpleInventory inventory = new SimpleInventory(entity.size());

        for (int i = 0; i < entity.size(); i++) {
            inventory.setStack(i, entity.getStack(i));
        }

        Optional<? extends BaseRecipe> recipe = Objects.requireNonNull(entity.getWorld()).getRecipeManager().getFirstMatch(recipeType, inventory, entity.getWorld());

        if (hasRecipe(entity) && recipe.isPresent()) {
            //TODO: IDK if it works
            for (int slot : inputSlots) {
                //entity.removeStack(slot, recipe.get().getIngredients().get(slot).getMatchingStacks()[slot].getCount());
                entity.removeStack(slot, 1);
            }

            entity.setStack(outputSlot, new ItemStack(recipe.get().getOutput().getItem(), entity.getStack(outputSlot).getCount() + recipe.get().getOutput().getCount()));

            entity.resetProgress();
        }
    }
}
