package com.diamantino.spacerevolution.blockentities.cables;

import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;
import com.diamantino.spacerevolution.initialization.ModBlockEntities;
import com.diamantino.spacerevolution.variants.CableVariants;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
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
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

import java.util.ArrayList;
import java.util.List;

// CREDIT: https://github.com/TechReborn/TechReborn
// UNDER MIT LICENSE: https://github.com/TechReborn/TechReborn/blob/1.19/LICENSE.md
// Modified by Diamantino
public class ElectricCableBlockEntity extends BlockEntity implements BlockEntityTicker<ElectricCableBlockEntity> {
    // Can't use SimpleEnergyStorage because the cable type is not available when the BE is constructed.
    public final SimpleSidedEnergyContainer energyContainer = new SimpleSidedEnergyContainer() {
        @Override
        public long getCapacity() {
            return getCableType().transferRate * 4L;
        }

        @Override
        public long getMaxInsert(Direction side) {
            if (allowTransfer(side)) return getCableType().transferRate;
            else return 0;
        }

        @Override
        public long getMaxExtract(Direction side) {
            if (allowTransfer(side)) return getCableType().transferRate;
            else return 0;
        }
    };
    private CableVariants.Electric cableType = null;
    long lastTick = 0;
    // null means that it needs to be re-queried
    List<CableTarget> targets = null;
    /**
     * Adjacent caches, used to quickly query adjacent cable block entities.
     */
    @SuppressWarnings("unchecked")
    private final BlockApiCache<EnergyStorage, Direction>[] adjacentCaches = new BlockApiCache[6];
    /**
     * Bitmask to prevent input or output into/from the cable when the cable already transferred in the target direction.
     * This prevents double transfer rates, and back and forth between two cables.
     */
    int blockedSides = 0;

    /**
     * This is only used during the cable tick, whereas {@link #blockedSides} is used between ticks.
     */
    boolean ioBlocked = false;

    public ElectricCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.electricCableBlockEntity, pos, state);
    }

    public ElectricCableBlockEntity(BlockPos pos, BlockState state, CableVariants.Electric type) {
        super(ModBlockEntities.electricCableBlockEntity, pos, state);
        this.cableType = type;
    }

    CableVariants.Electric getCableType() {
        if (cableType != null) {
            return cableType;
        }
        if (world == null) {
            return CableVariants.Electric.COPPER;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof ElectricCableBlock) {
            return ((ElectricCableBlock) block).type;
        }
        //Something has gone wrong if this happens
        return CableVariants.Electric.COPPER;
    }

    private boolean allowTransfer(Direction side) {
        return !ioBlocked && (blockedSides & (1 << side.ordinal())) == 0;
    }

    public EnergyStorage getSideEnergyStorage(@Nullable Direction side) {
        return energyContainer.getSideStorage(side);
    }

    public long getEnergy() {
        return energyContainer.amount;
    }

    public void setEnergy(long energy) {
        energyContainer.amount = energy;
    }

    private BlockApiCache<EnergyStorage, Direction> getAdjacentCache(Direction direction) {
        if (adjacentCaches[direction.getId()] == null) {
            assert world != null;
            adjacentCaches[direction.getId()] = BlockApiCache.create(EnergyStorage.SIDED, (ServerWorld) world, pos.offset(direction));
        }
        return adjacentCaches[direction.getId()];
    }

    @Nullable
    BlockEntity getAdjacentBlockEntity(Direction direction) {
        return getAdjacentCache(direction).getBlockEntity();
    }

    void appendTargets(List<OfferedEnergyStorage> targetStorages) {
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

                BlockApiCache<EnergyStorage, Direction> adjCache = getAdjacentCache(direction);

                if (adjCache.getBlockEntity() instanceof ElectricCableBlockEntity adjCable) {
                    if (adjCable.getCableType().transferRate == getCableType().transferRate) {
                        // Make sure cables are not used as regular targets.
                        foundSomething = true;
                    }
                } else if (adjCache.find(direction.getOpposite()) != null) {
                    foundSomething = true;
                    targets.add(new CableTarget(direction, adjCache));
                }

                newBlockState = newBlockState.with(ElectricCableBlock.PROPERTY_MAP.get(direction), foundSomething);
            }

            serverWorld.setBlockState(getPos(), newBlockState);
        }

        // Fill the list.
        for (CableTarget target : targets) {
            EnergyStorage storage = target.find();

            if (storage == null) {
                // Schedule a rebuild next tick.
                // This is just a reference change, the iterator remains valid.
                targets = null;
            } else {
                targetStorages.add(new OfferedEnergyStorage(this, target.directionTo, storage));
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
        if (compound.contains("energy")) {
            energyContainer.amount = compound.getLong("energy");
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putLong("energy", energyContainer.amount);
    }

    public void neighborUpdate() {
        targets = null;
    }

    // BlockEntityTicker
    @Override
    public void tick(World world, BlockPos pos, BlockState state, ElectricCableBlockEntity blockEntity2) {
        if (world == null || world.isClient) {
            return;
        }

        ElectricCableTickManager.handleCableTick(this);
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

    private record CableTarget(Direction directionTo, BlockApiCache<EnergyStorage, Direction> cache) {
        @Nullable
        EnergyStorage find() {
            return cache.find(directionTo.getOpposite());
        }
    }
}
