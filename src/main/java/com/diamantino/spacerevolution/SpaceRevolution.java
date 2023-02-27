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
        ModMessages.registerC2SPackets();
        ModFeatures.registerModFeatures();
        //TODO: Add asteroid entities that when hit the ground, they spawn a crater with the roid at the center
    }

    public void onRegisterReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> registry) {
        registry.accept(new ResourceLocation(AdAstra.MOD_ID, "planet_data"), new PlanetData());
    }
}
