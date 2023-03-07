package com.diamantino.spacerevolution.client.renderers.entities;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.client.models.entities.AsteroidEntityModel;
import com.diamantino.spacerevolution.entities.AsteroidEntity;
import com.diamantino.spacerevolution.initialization.ModReferences;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class AsteroidEntityRenderer extends LivingEntityRenderer<AsteroidEntity, AsteroidEntityModel> {
    public AsteroidEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new AsteroidEntityModel(ctx.getPart(SpaceRevolutionClient.asteroidLayer)), 0);
    }

    @Override
    public void render(AsteroidEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);

        matrixStack.push();

        model.setAngles(livingEntity, 0, 0, i, 0, 0);

        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(AsteroidEntity entity) {
        return new Identifier(ModReferences.modId, "textures/entity/asteroid.png");
    }
}
