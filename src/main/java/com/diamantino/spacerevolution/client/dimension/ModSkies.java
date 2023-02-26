package com.diamantino.spacerevolution.client.dimension;

import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModSkies {
    public static List<PlanetSkyRenderer> skyRenderers = new ArrayList<>();

    public static void register() {
        for (PlanetSkyRenderer skyRenderer : skyRenderers) {
            registerDimensionEffects(skyRenderer.dimension(), new DimensionEffects(skyRenderer));
        }
    }

    public static void registerDimensionEffects(RegistryKey<World> id, DimensionEffects effects) {

    }
}