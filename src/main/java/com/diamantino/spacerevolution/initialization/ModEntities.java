package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.entities.AsteroidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static EntityType<AsteroidEntity> asteroidEntityType = Registry.register(Registries.ENTITY_TYPE, new Identifier(ModReferences.modId, "asteroid", new EntityType<AsteroidEntity>( )))

    public static void registerModEntities() {
        ModReferences.logger.debug("Registering ModEntities for " + ModReferences.modId);
    }
}
