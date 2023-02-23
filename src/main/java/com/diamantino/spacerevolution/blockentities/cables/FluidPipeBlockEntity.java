package com.diamantino.spacerevolution.blockentities.cables;

import com.diamantino.spacerevolution.blocks.cables.FluidPipeBlock;
import com.diamantino.spacerevolution.initialization.ModBlockEntities;
import com.diamantino.spacerevolution.variants.CableVariants;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluidPipeBlockEntity extends BlockEntity implements BlockEntityTicker<FluidPipeBlockEntity> {
    // Can't use SimpleEnergyStorage because the cable type is not available when the BE is constructed.
    public final SingleVariantStorage<FluidVariant> fluidContainer = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return getCableType().transferRate * 4L;
        }
    };
    private CableVariants.Fluid cableType = null;
    long lastTick = 0;
    // null means that it needs to be re-queried
    List<FluidPipeBlockEntity.CableTarget> targets = null;
    /**
     * Adjacent caches, used to quickly query adjacent cable block entities.
     */
    @SuppressWarnings("unchecked")
    private final BlockApiCache<Storage<FluidVariant>, Direction>[] adjacentCaches = new BlockApiCache[6];
    /**
     * Bitmask to prevent input or output into/from the cable when the cable already transferred in the target direction.
     * This prevents double transfer rates, and back and forth between two cables.
     */
    int blockedSides = 0;

    /**
     * This is only used during the cable tick, whereas {@link #blockedSides} is used between ticks.
     */
    boolean ioBlocked = false;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.electricCableBlockEntity, pos, state);
    }

    public FluidPipeBlockEntity(BlockPos pos, BlockState state, CableVariants.Fluid type) {
        super(ModBlockEntities.electricCableBlockEntity, pos, state);
        this.cableType = type;
    }

    CableVariants.Fluid getCableType() {
        if (cableType != null) {
            return cableType;
        }
        if (world == null) {
            return CableVariants.Fluid.BASIC;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof FluidPipeBlock) {
            return ((FluidPipeBlock) block).type;
        }
        //Something has gone wrong if this happens
        return CableVariants.Fluid.BASIC;
    }

    private boolean allowTransfer(Direction side) {
        return !ioBlocked && (blockedSides & (1 << side.ordinal())) == 0;
    }

    public SingleVariantStorage<FluidVariant> getFluidStorage() {
        return fluidContainer;
    }

    public long getEnergy() {
        return fluidContainer.amount;
    }

    public void setEnergy(long energy) {
        fluidContainer.amount = energy;
    }

    private BlockApiCache<Storage<FluidVariant>, Direction> getAdjacentCache(Direction direction) {
        if (adjacentCaches[direction.getId()] == null) {
            assert world != null;
            adjacentCaches[direction.getId()] = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, pos.offset(direction));
        }
        return adjacentCaches[direction.getId()];
    }

    @Nullable
    BlockEntity getAdjacentBlockEntity(Direction direction) {
        return getAdjacentCache(direction).getBlockEntity();
    }

    void appendTargets(List<OfferedFluidStorage> targetStorages) {
        ServerWorld serverWorld = (ServerWorld) world;
        if (serverWorld == null) {
            return;
        }

        // Update our targets if necessary.
        if (targets == null) {
            BlockState newBlockState = getCachedState();

            targets = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                boolean foundSomething = false;

                BlockApiCache<Storage<FluidVariant>, Direction> adjCache = getAdjacentCache(direction);

                if (adjCache.getBlockEntity() instanceof FluidPipeBlockEntity adjCable) {
                    if (adjCable.getCableType().transferRate == getCableType().transferRate) {
                        // Make sure cables are not used as regular targets.
                        foundSomething = true;
                    }
                } else if (adjCache.find(direction.getOpposite()) != null) {
                    foundSomething = true;
                    targets.add(new FluidPipeBlockEntity.CableTarget(direction, adjCache));
                }

                newBlockState = newBlockState.with(FluidPipeBlock.PROPERTY_MAP.get(direction), foundSomething);
            }

            serverWorld.setBlockState(getPos(), newBlockState);
        }

        // Fill the list.
        for (FluidPipeBlockEntity.CableTarget target : targets) {
            Storage<FluidVariant> storage = target.find();

            if (storage == null) {
                // Schedule a rebuild next tick.
                // This is just a reference change, the iterator remains valid.
                targets = null;
            } else {
                targetStorages.add(new OfferedFluidStorage(this, target.directionTo, (SingleVariantStorage<FluidVariant>) storage));
            }
        }

        // Reset blocked sides.
        blockedSides = 0;
    }

    // BlockEntity
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        NbtCompound nbtTag = new NbtCompound();
        writeNbt(nbtTag);
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        if (compound.contains("fluid")) {
            fluidContainer.amount = compound.getLong("fluid");
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putLong("fluid", fluidContainer.amount);
    }

    public void neighborUpdate() {
        targets = null;
    }

    // BlockEntityTicker
    @Override
    public void tick(World world, BlockPos pos, BlockState state, FluidPipeBlockEntity blockEntity2) {
        if (world == null || world.isClient) {
            return;
        }

        FluidPipeTickManager.handleCableTick(this);
    }

    // IListInfoProvider
    /*@Override
    public void addInfo(List<Text> info, boolean isReal, boolean hasData) {
        info.add(Text.translatable("spacerevolution.tooltip.transferRate").formatted(Formatting.GRAY).append(": ").append(PowerSystem.getLocalizedPower(getCableType().transferRate)).formatted(Formatting.GOLD).append("/t"));
        info.add(Text.translatable("spacerevolution.tooltip.tier").formatted(Formatting.GRAY).append(": ").append(Text.literal(StringUtils.toFirstCapitalAllLowercase(getCableType().tier.toString())).formatted(Formatting.GOLD)));

        if (!getCableType().canKill) {
            info.add(Text.translatable("spacerevolution.tooltip.cable.can_cover").formatted(Formatting.GRAY));
        }
    }*/

    private record CableTarget(Direction directionTo, BlockApiCache<Storage<FluidVariant>, Direction> cache) {
        @Nullable
        Storage<FluidVariant> find() {
            return cache.find(directionTo.getOpposite());
        }
    }
}