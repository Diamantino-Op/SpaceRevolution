package com.diamantino.spacerevolution.client.dimension;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.client.dimension.renderer.DimensionEffects;
import com.diamantino.spacerevolution.client.resourcepack.PlanetSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModDimensionEffects {
    public static Map<RegistryKey<World>, DimensionEffects> dimensionEffects = new LinkedHashMap<>();

    public static void register() {
        for (PlanetSkyRenderer skyRenderer : SpaceRevolutionClient.skyRenderers) {
            registerDimensionEffects(skyRenderer.dimension(), new DimensionEffects(skyRenderer));
        }
    }

    public static void registerDimensionEffects(RegistryKey<World> id, DimensionEffects effects) {
        dimensionEffects.put(id, effects);
    }
}