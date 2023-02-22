package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.blockentities.CrusherBlockEntity;
import com.diamantino.spacerevolution.blockentities.cables.ElectricCableBlockEntity;
import com.diamantino.spacerevolution.blocks.BaseMachineBlock;
import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public class ModBlockEntities {
    public static BlockEntityType<CrusherBlockEntity> crusherBlockEntity;
    public static BlockEntityType<ElectricCableBlockEntity> electricCableBlockEntity;

    public static void registerBlockEntities() {
        crusherBlockEntity = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ModReferences.modId, "crusher"), FabricBlockEntityTypeBuilder.create(CrusherBlockEntity::new, ModBlocks.crusherBlock).build(null));

        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, context) -> {
            Direction localDir = Objects.requireNonNull(blockEntity.getWorld()).getBlockState(blockEntity.getPos()).get(BaseMachineBlock.FACING);

            if (context == Direction.UP || context == Direction.DOWN)
                return null;

            return switch (localDir) {
                default -> context.getOpposite() == Direction.NORTH ? blockEntity.energyStorage.getSideStorage(Direction.SOUTH) : null;
                case EAST -> context.rotateYClockwise() == Direction.NORTH ? blockEntity.energyStorage.getSideStorage(Direction.SOUTH) : null;
                case SOUTH -> context == Direction.NORTH ? blockEntity.energyStorage.getSideStorage(Direction.SOUTH) : null;
                case WEST -> context.rotateYCounterclockwise() == Direction.NORTH ? blockEntity.energyStorage.getSideStorage(Direction.SOUTH) : null;
            };
        }, crusherBlockEntity);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, context) -> context == Direction.UP ? blockEntity.fluidStorages.getStorage(0) : null, crusherBlockEntity);

        electricCableBlockEntity = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ModReferences.modId, "electric_cable"), FabricBlockEntityTypeBuilder.create(ElectricCableBlockEntity::new, ModBlocks.electricCables.toArray(Block[]::new)).build(null));
        EnergyStorage.SIDED.registerForBlockEntity(ElectricCableBlockEntity::getSideEnergyStorage, electricCableBlockEntity);

        ModReferences.logger.debug("Registering ModEntities for " + ModReferences.modId);
    }
}
