package com.diamantino.spacerevolution.networking.packet;

import com.diamantino.spacerevolution.blockentities.BaseMachineBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class OutputStackSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItemStack();
        BlockPos position = buf.readBlockPos();

        assert client.world != null;
        if(client.world.getBlockEntity(position) instanceof BaseMachineBlockEntity blockEntity) {
            blockEntity.setOutputStack(stack);
        }
    }
}
