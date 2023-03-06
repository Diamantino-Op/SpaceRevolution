package com.diamantino.spacerevolution.initialization;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticleTypes {
    public static final DefaultParticleType planetRain = Registry.register(Registries.PARTICLE_TYPE, "planet_rain", new DefaultParticleType(true) {});

    public static void registerModParticleTypes() {
        ModReferences.logger.debug("Registering ModParticleTypes for " + ModReferences.modId);
    }
}
