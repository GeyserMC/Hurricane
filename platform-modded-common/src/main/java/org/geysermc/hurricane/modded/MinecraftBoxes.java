package org.geysermc.hurricane.modded;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.geysermc.hurricane.core.math.Aabb;
import org.geysermc.hurricane.core.math.BlockPosition;

public final class MinecraftBoxes {
    private MinecraftBoxes() {
    }

    public static VoxelShape toLocalShape(Aabb box, BlockPosition blockPosition) {
        return Shapes.box(
                box.minX() - blockPosition.x(),
                box.minY() - blockPosition.y(),
                box.minZ() - blockPosition.z(),
                box.maxX() - blockPosition.x(),
                box.maxY() - blockPosition.y(),
                box.maxZ() - blockPosition.z()
        );
    }
}
