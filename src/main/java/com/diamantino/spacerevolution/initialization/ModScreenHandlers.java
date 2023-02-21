package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.client.screen.handlers.CrusherScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<CrusherScreenHandler> crusherScreenHandler = new ExtendedScreenHandlerType<>(CrusherScreenHandler::new);

    public static void registerScreenHandlers() {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(ModReferences.modId, "crusher_screen"), crusherScreenHandler);

        ModReferences.logger.debug("Registering ModItemGroups for " + ModReferences.modId);
    }
}
