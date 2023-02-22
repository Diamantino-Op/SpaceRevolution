package com.diamantino.spacerevolution.blockentities;

import com.diamantino.spacerevolution.blocks.BaseMachineBlock;
import com.diamantino.spacerevolution.client.screen.handlers.CrusherScreenHandler;
import com.diamantino.spacerevolution.initialization.ModBlockEntities;
import com.diamantino.spacerevolution.recipes.CrushingRecipe;
import com.diamantino.spacerevolution.utils.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CrusherBlockEntity extends BaseMachineBlockEntity {
    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.crusherBlockEntity, pos, state, "crusher", 4, 100, 1, CrushingRecipe.Type.instance, 10000, 100, 0, true, false, 50, List.of(Direction.SOUTH), 3, FluidStack.convertDropletsToMb(FluidConstants.BLOCK) * 10, 10, 2);

        inputSlots.add(0);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        Direction localDir = Objects.requireNonNull(this.getWorld()).getBlockState(this.pos).get(BaseMachineBlock.FACING);

        if (side == Direction.UP || side == Direction.DOWN)
            return false;

        assert side != null;
        return switch (localDir) {
            default -> side.getOpposite() == Direction.WEST && slot == 0;
            case EAST -> side.rotateYClockwise() == Direction.WEST && slot == 0;
            case SOUTH -> side == Direction.WEST && slot == 0;
            case WEST -> side.rotateYCounterclockwise() == Direction.WEST && slot == 0;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, @Nullable Direction side) {
        Direction localDir = Objects.requireNonNull(this.getWorld()).getBlockState(this.pos).get(BaseMachineBlock.FACING);

        if (side == Direction.UP || side == Direction.DOWN)
            return false;

        assert side != null;
        return switch (localDir) {
            default -> side.getOpposite() == Direction.EAST && slot == 0;
            case EAST -> side.rotateYClockwise() == Direction.EAST && slot == 0;
            case SOUTH -> side == Direction.EAST && slot == 0;
            case WEST -> side.rotateYCounterclockwise() == Direction.EAST && slot == 0;
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        sendEnergyPacket();
        sendFluidPacket();

        return new CrusherScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, CrusherBlockEntity entity) {
        if (world.isClient())
            return;

        //TODO: Finish this
        if (hasEnergyItemInSlot(entity)) {
            entity.addEnergy(16);
        }

        if (hasFluidStorageInSlot(entity)) {
            if (entity.addFluid(0, FluidVariant.of(Fluids.WATER), FluidStack.convertDropletsToMb(FluidConstants.BUCKET)))
                entity.setStack(2, new ItemStack(Items.BUCKET));
        }

        if (hasRecipe(entity)) {
            if (hasEnoughEnergy(entity)) {
                //TODO: Make different lubricants
                if (hasEnoughLubricant(entity)) {
                    entity.progress += 2;
                    entity.useFluid(0, FluidVariant.of(Fluids.WATER), entity.getLubricantUsage());
                } else {
                    entity.progress++;
                }

                entity.useEnergy(entity.getEnergyUsage());

                if (!state.get(BaseMachineBlock.ACTIVE)) {
                    state = state.with(BaseMachineBlock.ACTIVE, true);
                    world.setBlockState(blockPos, state, Block.NOTIFY_ALL);
                }

                markDirty(world, blockPos, state);

                if (entity.progress >= entity.maxProgress) {
                    entity.craftItem(entity);
                }
            } else {
                if (state.get(BaseMachineBlock.ACTIVE)) {
                    state = state.with(BaseMachineBlock.ACTIVE, false);
                    world.setBlockState(blockPos, state, Block.NOTIFY_ALL);
                }

                markDirty(world, blockPos, state);
            }
        } else {
            entity.resetProgress();

            if (state.get(BaseMachineBlock.ACTIVE)) {
                state = state.with(BaseMachineBlock.ACTIVE, false);
                world.setBlockState(blockPos, state, Block.NOTIFY_ALL);
            }

            updateOutputStack(entity, ItemStack.EMPTY);

            markDirty(world, blockPos, state);
        }
    }
}
