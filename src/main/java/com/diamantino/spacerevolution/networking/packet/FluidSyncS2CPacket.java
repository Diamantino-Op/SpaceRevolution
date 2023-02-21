package com.diamantino.spacerevolution.networking.packet;

import com.diamantino.spacerevolution.blockentities.BaseMachineBlockEntity;
import com.diamantino.spacerevolution.client.screen.handlers.BaseMachineScreenHandler;
import com.diamantino.spacerevolution.utils.FluidStack;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class FluidSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int count = buf.readInt();

        for (int i = 0; i < count; i++) {
            FluidVariant variant = FluidVariant.fromPacket(buf);
            long fluidLevel = buf.readLong();
            BlockPos position = buf.readBlockPos();

            assert client.world != null;
            if(client.world.getBlockEntity(position) instanceof BaseMachineBlockEntity blockEntity) {
                blockEntity.setFluidLevel(i, variant, fluidLevel);

                if(client.player.currentScreenHandler instanceof BaseMachineScreenHandler screenHandler && screenHandler.blockEntity.getPos().equals(position)) {
                    blockEntity.setFluidLevel(i, variant, fluidLevel);
                    screenHandler.setFluid(i, new FluidStack(variant, fluidLevel));
                }
            }
        }
    }
}
