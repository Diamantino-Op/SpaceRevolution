package com.diamantino.spacerevolution.networking.packet;

import com.diamantino.spacerevolution.data.PlanetData;
import com.diamantino.spacerevolution.initialization.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RequestPlanetDataC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        PacketByteBuf buf = PacketByteBufs.create();

        PlanetData.writePlanetData(buf);

        ServerPlayNetworking.send(serverPlayerEntity, ModMessages.returnPlanetDataPacket, buf);
    }
}
