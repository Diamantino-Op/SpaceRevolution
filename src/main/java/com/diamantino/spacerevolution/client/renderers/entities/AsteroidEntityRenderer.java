package com.diamantino.spacerevolution.client.renderers.entities;

import com.diamantino.spacerevolution.client.models.entities.AsteroidEntityModel;
import com.diamantino.spacerevolution.entities.AsteroidEntity;
import com.diamantino.spacerevolution.initialization.ModReferences;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class AsteroidEntityRenderer extends LivingEntityRenderer<AsteroidEntity, AsteroidEntityModel> {
    public AsteroidEntityRenderer(EntityRendererFactory.Context ctx, AsteroidEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    public Identifier getTexture(AsteroidEntity entity) {
        return new Identifier(ModReferences.modId, "textures/entity/asteroid.png");
    }
}
