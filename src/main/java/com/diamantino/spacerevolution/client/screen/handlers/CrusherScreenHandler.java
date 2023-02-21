package com.diamantino.spacerevolution.client.screen.handlers;

import com.diamantino.spacerevolution.initialization.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class CrusherScreenHandler extends BaseMachineScreenHandler {
    public CrusherScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(2));
    }

    public CrusherScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity entity, PropertyDelegate delegate) {
        super(ModScreenHandlers.crusherScreenHandler, syncId, 4, playerInventory, entity, delegate);

        this.addSlot(new Slot(inventory, 0, 58, 37));
        this.addSlot(new Slot(inventory, 1, 102, 37));

        this.addSlot(new Slot(inventory, 2, 32, 61));
        this.addSlot(new Slot(inventory, 3, 136, 61));
    }
}
