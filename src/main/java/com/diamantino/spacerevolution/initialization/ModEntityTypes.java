package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.entities.AsteroidEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {
    public static EntityType<AsteroidEntity> asteroidEntityType = Registry.register(Registries.ENTITY_TYPE, new Identifier(ModReferences.modId, "asteroid"), FabricEntityTypeBuilder.<AsteroidEntity>create(SpawnGroup.MISC, AsteroidEntity::new).dimensions(EntityDimensions.fixed(18, 9f)).build());

    public static void registerModEntityTypes() {
        FabricDefaultAttributeRegistry.register(asteroidEntityType, AsteroidEntity.setAttributes());

        ModReferences.logger.debug("Registering ModEntityTypes for " + ModReferences.modId);
    }
}
