package com.diamantino.spacerevolution.networking.packet;

import com.diamantino.spacerevolution.blockentities.BaseMachineBlockEntity;
import com.diamantino.spacerevolution.client.screen.handlers.BaseMachineScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class EnergySyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        long energy = buf.readLong();
        BlockPos pos = buf.readBlockPos();

        assert client.world != null;
        if (client.world.getBlockEntity(pos) instanceof BaseMachineBlockEntity blockEntity) {
            blockEntity.setEnergyLevel(energy);

            if (client.player.currentScreenHandler instanceof BaseMachineScreenHandler screenHandler && screenHandler.blockEntity.getPos().equals(pos)) {
                blockEntity.setEnergyLevel(energy);
            }
        }
    }
}
