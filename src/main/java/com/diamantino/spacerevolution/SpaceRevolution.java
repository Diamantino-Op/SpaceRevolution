package com.diamantino.spacerevolution;

import com.diamantino.spacerevolution.initialization.*;
import net.fabricmc.api.ModInitializer;

public class SpaceRevolution implements ModInitializer {
    @Override
    public void onInitialize() {
        ModReferences.registerModReferences();
        ModItemGroups.registerItemGroups();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
        ModRecipes.registerRecipes();
        ModMessages.registerS2CPackets();
        ModMessages.registerC2SPackets();
    }
}
