package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.world.features.SpaceAsteroidFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<DefaultFeatureConfig> spaceAsteroidsFeature = Registry.register(Registries.FEATURE, new Identifier(ModReferences.modId, "space_asteroids"), new SpaceAsteroidFeature(DefaultFeatureConfig.CODEC));

    public static void registerModFeatures() {
        ModReferences.logger.debug("Registering ModFeatures for " + ModReferences.modId);
    }
}
