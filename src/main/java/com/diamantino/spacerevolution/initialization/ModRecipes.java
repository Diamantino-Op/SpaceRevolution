package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.recipes.CrushingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void registerRecipes() {
        //Crushing
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(ModReferences.modId, CrushingRecipe.Serializer.id), CrushingRecipe.Serializer.instance);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(ModReferences.modId, CrushingRecipe.Type.id), CrushingRecipe.Type.instance);

        ModReferences.logger.debug("Registering ModRecipes for " + ModReferences.modId);
    }
}
