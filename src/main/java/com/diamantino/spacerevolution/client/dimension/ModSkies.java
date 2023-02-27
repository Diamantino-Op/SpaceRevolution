package com.diamantino.spacerevolution.client.dimension;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.client.dimension.renderer.DimensionEffects;
import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ModSkies {
    public static void register() {
        for (PlanetSkyRenderer skyRenderer : SpaceRevolutionClient.skyRenderers) {
            registerDimensionEffects(skyRenderer.dimension(), new DimensionEffects(skyRenderer));
        }
    }

    public static void registerDimensionEffects(RegistryKey<World> id, DimensionEffects effects) {

    }
}