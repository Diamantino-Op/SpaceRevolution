package com.diamantino.spacerevolution.client.renderers.entities;

import com.diamantino.spacerevolution.entities.BlockyVehicleEntity;
import com.diamantino.spacerevolution.storage.StructureStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class BlockyVehicleEntityRenderer<E extends BlockyVehicleEntity> extends EntityRenderer<E> {
    BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

    protected BlockyVehicleEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(E entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        StructureStorage structureStorage = entity.getStructureStorage();

        if (structureStorage == null)
            return;

        float halfX = structureStorage.getSizeX() / 2f;
        float halfY = structureStorage.getSizeY() / 2f;
        float halfZ = structureStorage.getSizeZ() / 2f;

        matrices.push();

        matrices.translate((float)0, halfY, (float)0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw()));
        matrices.translate(-halfX, (float)0 - halfY, -halfZ);

        for(int x = 0; x < structureStorage.getSizeX(); x++) {
            for(int y = 0; y < structureStorage.getSizeY(); y++) {
                for(int z = 0; z < structureStorage.getSizeZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState block  = structureStorage.getBlockState(pos);
                    BlockEntity blockEntity = BlockEntity.createFromNbt(pos, block, structureStorage.getBlockEntityNbt(pos));
                    matrices.push();

                    matrices.translate(x, y, z);

                    try {
                        blockRenderManager.renderBlock(block, new BlockPos(x, y, z), entity.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), true, entity.world.getRandom());
                        blockEntityRenderDispatcher.render(blockEntity, tickDelta, matrices, vertexConsumers);
                    }
                    catch (NullPointerException ignored) {}

                    matrices.pop();
                }
            }
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(E entity) {
        return null;
    }
}
