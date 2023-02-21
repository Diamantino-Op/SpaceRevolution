package com.diamantino.spacerevolution.client.renderers.blockentities;

import com.diamantino.spacerevolution.blockentities.CrusherBlockEntity;
import com.diamantino.spacerevolution.blocks.BaseMachineBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Objects;

public class CrusherBlockEntityRenderer implements BlockEntityRenderer<CrusherBlockEntity> {
    public CrusherBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(CrusherBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getRenderStack();

        matrices.push();

        switch (entity.getCachedState().get(BaseMachineBlock.FACING)) {
            case NORTH -> {
                matrices.translate(0.5f, 0.5f, 0.115f);
                matrices.scale(0.3f, 0.3f, 0.3f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.5f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }

            case EAST -> {
                matrices.translate(0.885f, 0.5f, 0.5f);
                matrices.scale(0.3f, 0.3f, 0.3f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(22.5f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            }

            case SOUTH -> {
                matrices.translate(0.5f, 0.5f, 0.885f);
                matrices.scale(0.3f, 0.3f, 0.3f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }

            case WEST -> {
                matrices.translate(0.115f, 0.5f, 0.5f);
                matrices.scale(0.3f, 0.3f, 0.3f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-22.5f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            }
        }

        itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GUI, getLightLevel(Objects.requireNonNull(entity.getWorld()), entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);

        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
