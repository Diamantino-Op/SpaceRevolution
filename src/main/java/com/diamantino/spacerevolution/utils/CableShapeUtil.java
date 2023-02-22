package com.diamantino.spacerevolution.utils;

import com.diamantino.spacerevolution.blocks.cables.ElectricCableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CableShapeUtil {
    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new IdentityHashMap<>();

    public CableShapeUtil() {
    }

    private static VoxelShape getStateShape(BlockState state) {
        ElectricCableBlock cableBlock = (ElectricCableBlock)state.getBlock();
        double size = cableBlock.type.cableThickness;
        VoxelShape baseShape = VoxelShapes.cuboid(size, size, size, 1.0 - size, 1.0 - size, 1.0 - size);
        List<VoxelShape> connections = new ArrayList<>();
        Direction[] var6 = Direction.values();

        for (Direction dir : var6) {
            if (state.get(ElectricCableBlock.PROPERTY_MAP.get(dir))) {
                double[] mins = new double[]{size, size, size};
                double[] maxs = new double[]{1.0 - size, 1.0 - size, 1.0 - size};
                int axis = dir.getAxis().ordinal();
                if (dir.getDirection() == Direction.AxisDirection.POSITIVE) {
                    maxs[axis] = 1.0;
                } else {
                    mins[axis] = 0.0;
                }

                connections.add(VoxelShapes.cuboid(mins[0], mins[1], mins[2], maxs[0], maxs[1], maxs[2]));
            }
        }

        return VoxelShapes.union(baseShape, connections.toArray(new VoxelShape[0]));
    }

    public static VoxelShape getShape(BlockState state) {
        return SHAPE_CACHE.computeIfAbsent(state, CableShapeUtil::getStateShape);
    }
}