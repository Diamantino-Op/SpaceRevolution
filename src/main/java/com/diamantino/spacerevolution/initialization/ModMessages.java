package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.networking.packet.*;
import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    //C2S
    public static final Identifier requestPlanetDataPacket = new Identifier(ModReferences.modId, "request_planet_data");

    //S2C
    public static final Identifier energySyncPacket = new Identifier(ModReferences.modId, "energy_sync");
    public static final Identifier fluidSyncPacket = new Identifier(ModReferences.modId, "fluid_sync");
    public static final Identifier inventorySyncPacket = new Identifier(ModReferences.modId, "inventory_sync");
    public static final Identifier outputStackSyncPacket = new Identifier(ModReferences.modId, "output_stack_sync");
    public static final Identifier returnPlanetDataPacket = new Identifier(ModReferences.modId, "return_planet_data");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(requestPlanetDataPacket, RequestPlanetDataC2SPacket::receive);

        ModReferences.logger.debug("Registering ModMessagesC2S for " + ModReferences.modId);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(energySyncPacket, EnergySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(fluidSyncPacket, FluidSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(inventorySyncPacket, InventorySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(outputStackSyncPacket, OutputStackSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(returnPlanetDataPacket, ReturnPlanetDataS2CPacket::receive);

        ModReferences.logger.debug("Registering ModMessagesS2C for " + ModReferences.modId);
    }
}
