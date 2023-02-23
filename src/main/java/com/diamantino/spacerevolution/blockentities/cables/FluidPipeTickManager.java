package com.diamantino.spacerevolution.blockentities.cables;

import com.diamantino.spacerevolution.variants.CableVariants;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

import java.util.*;

public class FluidPipeTickManager {
    private static final List<FluidPipeBlockEntity> cableList = new ArrayList<>();
    private static final List<OfferedFluidStorage> targetStorages = new ArrayList<>();
    private static final Deque<FluidPipeBlockEntity> bfsQueue = new ArrayDeque<>();
    private static long tickCounter = 0;

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> tickCounter++);
    }

    static void handleCableTick(FluidPipeBlockEntity startingCable) {
        if (!(startingCable.getWorld() instanceof ServerWorld)) throw new IllegalStateException();

        try {
            gatherCables(startingCable);
            if (cableList.size() == 0) return;

            // Group all energy into the network.
            long networkCapacity = 0;
            long networkAmount = 0;

            for (FluidPipeBlockEntity cable : cableList) {
                networkAmount += cable.fluidContainer.amount;
                networkCapacity += cable.fluidContainer.getCapacity();

                // Update cable connections.
                cable.appendTargets(targetStorages);
                // Block any cable I/O while we access the network amount directly.
                // Some things might try to access cables, for example a p2p tunnel pointing back at a cable.
                // If the cables and the network go out of sync, we risk duping or voiding energy.
                cable.ioBlocked = true;
            }

            // Just in case.
            if (networkAmount > networkCapacity) {
                networkAmount = networkCapacity;
            }

            // Pull energy from storages.
            networkAmount += dispatchTransfer(startingCable.getCableType(), SingleVariantStorage::extract, networkCapacity - networkAmount);
            // Push energy into storages.
            networkAmount -= dispatchTransfer(startingCable.getCableType(), SingleVariantStorage::insert, networkAmount);

            // Split energy evenly across cables.
            int cableCount = cableList.size();
            for (FluidPipeBlockEntity cable : cableList) {
                cable.fluidContainer.amount = networkAmount / cableCount;
                networkAmount -= cable.fluidContainer.amount;
                cableCount--;
                cable.markDirty();
                cable.ioBlocked = false;
            }
        } finally {
            cableList.clear();
            targetStorages.clear();
            bfsQueue.clear();
        }
    }

    private static boolean shouldTickCable(FluidPipeBlockEntity current) {
        // Make sure we only gather and tick each cable once per tick.
        if (current.lastTick == tickCounter) return false;
        // Make sure we ignore cables in non-ticking chunks.
        return current.getWorld() instanceof ServerWorld sw && sw.isChunkLoaded(current.getPos());
    }

    /**
     * Perform a BFS to gather all connected ticking cables.
     */
    private static void gatherCables(FluidPipeBlockEntity start) {
        if (!shouldTickCable(start)) return;

        bfsQueue.add(start);
        start.lastTick = tickCounter;
        cableList.add(start);

        while (!bfsQueue.isEmpty()) {
            FluidPipeBlockEntity current = bfsQueue.removeFirst();

            for (Direction direction : Direction.values()) {
                if (current.getAdjacentBlockEntity(direction) instanceof FluidPipeBlockEntity adjCable && current.getCableType().transferRate == adjCable.getCableType().transferRate) {
                    if (shouldTickCable(adjCable)) {
                        bfsQueue.add(adjCable);
                        adjCable.lastTick = tickCounter;
                        cableList.add(adjCable);
                    }
                }
            }
        }
    }

    /**
     * Perform a transfer operation across a list of targets.
     */
    private static long dispatchTransfer(CableVariants.Fluid cableType, FluidPipeTickManager.TransferOperation operation, long maxAmount) {
        // Build target list.
        List<FluidPipeTickManager.SortableStorage> sortedTargets = new ArrayList<>();
        for (var storage : targetStorages) {
            sortedTargets.add(new FluidPipeTickManager.SortableStorage(operation, storage));
        }
        // Shuffle for better average transfer.
        Collections.shuffle(sortedTargets);
        // Sort by lowest simulation target.
        sortedTargets.sort(Comparator.comparingLong(sortableStorage -> sortableStorage.simulationResult));
        // Actually perform the transfer.
        try (Transaction transaction = Transaction.openOuter()) {
            long transferredAmount = 0;
            for (int i = 0; i < sortedTargets.size(); ++i) {
                FluidPipeTickManager.SortableStorage target = sortedTargets.get(i);
                int remainingTargets = sortedTargets.size() - i;
                long remainingAmount = maxAmount - transferredAmount;
                // Limit max amount to the cable transfer rate.
                long targetMaxAmount = Math.min(remainingAmount / remainingTargets, cableType.transferRate);

                long localTransferred = operation.transfer(target.storage.storage, target.storage.storage.variant, targetMaxAmount, transaction);
                if (localTransferred > 0) {
                    transferredAmount += localTransferred;
                    // Block duplicate operations.
                    target.storage.afterTransfer();
                }
            }
            transaction.commit();
            return transferredAmount;
        }
    }

    private interface TransferOperation {
        long transfer(SingleVariantStorage<FluidVariant> storage, FluidVariant variant, long maxAmount, Transaction transaction);
    }

    private static class SortableStorage {
        private final OfferedFluidStorage storage;
        private final long simulationResult;

        //TODO: only transfer fluid if it is equal or empty

        SortableStorage(FluidPipeTickManager.TransferOperation operation, OfferedFluidStorage storage) {
            this.storage = storage;
            try (Transaction tx = Transaction.openOuter()) {
                this.simulationResult = operation.transfer(storage.storage, storage.storage.variant, Long.MAX_VALUE, tx);
            }
        }
    }
}
