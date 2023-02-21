package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.networking.packet.EnergySyncS2CPacket;
import com.diamantino.spacerevolution.networking.packet.FluidSyncS2CPacket;
import com.diamantino.spacerevolution.networking.packet.InventorySyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    //C2S

    //S2C
    public static final Identifier energySyncPacket = new Identifier(ModReferences.modId, "energy_sync");
    public static final Identifier fluidSyncPacket = new Identifier(ModReferences.modId, "fluid_sync");
    public static final Identifier inventorySyncPacket = new Identifier(ModReferences.modId, "inventory_sync");

    public static void registerC2SPackets() {


        ModReferences.logger.debug("Registering ModMessagesC2S for " + ModReferences.modId);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(energySyncPacket, EnergySyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(fluidSyncPacket, FluidSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(inventorySyncPacket, InventorySyncS2CPacket::receive);

        ModReferences.logger.debug("Registering ModMessagesS2C for " + ModReferences.modId);
    }
}
