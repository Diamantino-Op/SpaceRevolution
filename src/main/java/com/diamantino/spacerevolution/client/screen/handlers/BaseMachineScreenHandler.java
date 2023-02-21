package com.diamantino.spacerevolution.client.screen.handlers;

import com.diamantino.spacerevolution.blockentities.BaseMachineBlockEntity;
import com.diamantino.spacerevolution.utils.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMachineScreenHandler extends ScreenHandler {
    final Inventory inventory;
    final PropertyDelegate propertyDelegate;
    public final BaseMachineBlockEntity blockEntity;
    public List<FluidStack> fluidStacks;

    public BaseMachineScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, int inventorySize, PlayerInventory playerInventory, BlockEntity entity, PropertyDelegate delegate) {
        super(type, syncId);

        checkSize((Inventory) entity, inventorySize);

        this.inventory = (Inventory) entity;

        inventory.onOpen(playerInventory.player);

        this.propertyDelegate = delegate;

        this.blockEntity = (BaseMachineBlockEntity) entity;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(delegate);

        fluidStacks = new ArrayList<>(((BaseMachineBlockEntity) entity).fluidStorages.getSize());

        for (int i = 0; i < ((BaseMachineBlockEntity) entity).fluidStorages.getSize(); i++) {
            SingleVariantStorage<FluidVariant> storage = ((BaseMachineBlockEntity) entity).fluidStorages.getStorage(i);
            fluidStacks.add(i, new FluidStack(storage.variant, storage.amount));
        }
    }

    public void setFluid(int index, FluidStack fluidStack) {
        fluidStacks.set(index, fluidStack);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaleProgress(int progressArrowSize) {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 92 + i * 18));
            }
        }
    }

    void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 150));
        }
    }
}
