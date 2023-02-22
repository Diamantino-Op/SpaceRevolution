package com.diamantino.spacerevolution.datagen;

import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;
import com.diamantino.spacerevolution.initialization.ModBlocks;
import com.diamantino.spacerevolution.initialization.ModReferences;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (Block block : ModBlocks.electricCables) {
            generateCable(block, blockStateModelGenerator);
        }
    }

    public void generateCable(Block block, BlockStateModelGenerator blockStateModelGenerator) {
        MultipartBlockStateSupplier builder = MultipartBlockStateSupplier.create(block);

        Identifier coreCableIdentifier = TexturedModel.makeFactory(ModModelProvider::cableTexture, fromParent("cables/base_cable_core", TextureKey.of("cable"))).upload(block, blockStateModelGenerator.modelCollector);

        builder.with(BlockStateVariant.create().put(VariantSettings.MODEL, coreCableIdentifier));

        Identifier sideCableIdentifier = TexturedModel.makeFactory(ModModelProvider::cableTexture, fromParent("cables/base_cable_side", TextureKey.of("cable"))).upload(block, blockStateModelGenerator.modelCollector);

        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));

        directions.forEach(direction -> {
            switch (direction) {
                case NORTH -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier);
                    builder.with(When.create().set(ElectricCableBlock.NORTH, true), variant);
                }
                case SOUTH -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier).put(VariantSettings.Y, VariantSettings.Rotation.R180);
                    builder.with(When.create().set(ElectricCableBlock.SOUTH, true), variant);
                }
                case EAST -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier).put(VariantSettings.Y, VariantSettings.Rotation.R270);
                    builder.with(When.create().set(ElectricCableBlock.EAST, true), variant);
                }
                case WEST -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                    builder.with(When.create().set(ElectricCableBlock.WEST, true), variant);
                }
                case UP -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier).put(VariantSettings.X, VariantSettings.Rotation.R90);
                    builder.with(When.create().set(ElectricCableBlock.UP, true), variant);
                }
                case DOWN -> {
                    BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, sideCableIdentifier).put(VariantSettings.X, VariantSettings.Rotation.R270);
                    builder.with(When.create().set(ElectricCableBlock.DOWN, true), variant);
                }
            }
        });

        blockStateModelGenerator.blockStateCollector.accept(builder);
    }

    private Model fromParent(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier(ModReferences.modId, "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    public static TextureMap cableTexture(Block block) {
        ModReferences.logger.warn(Registries.BLOCK.getId(block));
        return new TextureMap().put(TextureKey.of("cable"), new Identifier(ModReferences.modId, "block/cables/" + Registries.BLOCK.getId(block)));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
