package com.diamantino.spacerevolution.networking.packet;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.data.PlanetData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class ReturnPlanetDataS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PlanetData.readPlanetData(buf);
        SpaceRevolutionClient.hasUpdatedPlanets = true;
    }
}
