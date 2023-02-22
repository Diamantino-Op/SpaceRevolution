package com.diamantino.spacerevolution.blocks.cables;

import com.diamantino.spacerevolution.blockentities.cables.ElectricCableBlockEntity;
import com.diamantino.spacerevolution.utils.CableShapeUtil;
import com.diamantino.spacerevolution.variants.CableVariants;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// CREDIT: https://github.com/TechReborn/TechReborn
// UNDER MIT LICENSE: https://github.com/TechReborn/TechReborn/blob/1.19/LICENSE.md
// Modified by Diamantino
public class ElectricCableBlock extends BlockWithEntity implements Waterloggable {

    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty COVERED = BooleanProperty.of("covered");

    public static final Map<Direction, BooleanProperty> PROPERTY_MAP = Util.make(new HashMap<>(), map -> {
        map.put(Direction.EAST, EAST);
        map.put(Direction.WEST, WEST);
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    });

    public final CableVariants.Electric type;

    public ElectricCableBlock(CableVariants.Electric type) {
        super(Block.Settings.of(Material.STONE).strength(1f, 8f));
        this.type = type;
        setDefaultState(this.getStateManager().getDefaultState().with(EAST, false).with(WEST, false).with(NORTH, false).with(SOUTH, false).with(UP, false).with(DOWN, false).with(WATERLOGGED, false).with(COVERED, false));
    }

    // BlockWithEntity
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricCableBlockEntity(pos, state, type);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> ((ElectricCableBlockEntity) blockEntity).tick(world1, pos, state1, (ElectricCableBlockEntity) blockEntity);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EAST, WEST, NORTH, SOUTH, UP, DOWN, WATERLOGGED, COVERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState()
                .with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState ourState, Direction direction, BlockState otherState,
                                                WorldAccess worldIn, BlockPos ourPos, BlockPos otherPos) {
        if (ourState.get(WATERLOGGED)) {
            worldIn.scheduleFluidTick(ourPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return ourState;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.getBlockEntity(pos) instanceof ElectricCableBlockEntity cable) {
            cable.neighborUpdate();
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext shapeContext) {
        if (state.get(COVERED)) {
            return VoxelShapes.fullCube();
        }
        return CableShapeUtil.getShape(state);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return CableShapeUtil.getShape(state);
    }

    /*@SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (!type.canKill) {
            return;
        }
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }
        if (!(blockEntity instanceof ElectricCableBlockEntity blockEntityCable)) {
            return;
        }

        if (blockEntityCable.getEnergy() <= 0) {
            return;
        }

        if (!CableElectrocutionEvent.EVENT.invoker().electrocute((LivingEntity) entity, type, pos, world, blockEntityCable)) {
            return;
        }

        if (TechRebornConfig.uninsulatedElectrocutionDamage) {
            if (type == TRContent.Cables.HV) {
                entity.setOnFireFor(1);
            }
            entity.damage(new ElectricalShockSource(), 1F);
            blockEntityCable.setEnergy(0);
        }
        if (TechRebornConfig.uninsulatedElectrocutionSound) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.CABLE_SHOCK, SoundCategory.BLOCKS,
                    0.6F, 1F);
        }
        if (TechRebornConfig.uninsulatedElectrocutionParticles) {
            world.addParticle(ParticleTypes.CRIT, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
        }
    }*/

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return !state.get(COVERED) && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.get(COVERED) && Waterloggable.super.canFillWithFluid(view, pos, state, fluid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        if (state.get(COVERED)) {
            final BlockState cover;

            if (((RenderAttachedBlockView) renderView).getBlockEntityRenderAttachment(pos) instanceof BlockState blockState) {
                cover = blockState;
            } else {
                cover = Blocks.OAK_PLANKS.getDefaultState();
            }

            return cover;
        }

        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }
}