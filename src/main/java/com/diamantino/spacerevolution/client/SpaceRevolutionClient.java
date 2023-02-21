package com.diamantino.spacerevolution.client;

import com.diamantino.spacerevolution.client.renderers.blockentities.CrusherBlockEntityRenderer;
import com.diamantino.spacerevolution.client.screen.CrusherScreen;
import com.diamantino.spacerevolution.initialization.ModBlockEntities;
import com.diamantino.spacerevolution.initialization.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class SpaceRevolutionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.crusherScreenHandler, CrusherScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.crusherBlockEntity, CrusherBlockEntityRenderer::new);
    }
}
