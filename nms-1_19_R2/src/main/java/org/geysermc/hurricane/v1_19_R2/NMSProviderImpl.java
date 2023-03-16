package org.geysermc.hurricane.v1_19_R2;

import org.geysermc.hurricane.NMSProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftAnimals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;

import java.util.Map;

public class NMSProviderImpl implements NMSProvider {
    @Override
    public void forEachPlayerOnSteerable(Entity entity, Map.Entry<Player, Object> entry) {
        final Mob impl = ((CraftAnimals) entity).getHandle();
        // Replicate ItemSteerable
        if (impl.isVehicle() && impl.hasControllingPassenger()) {
            // Used to mitigate the server setting delta movement to zero
            impl.setDeltaMovement((Vec3) entry.getValue());
            float steeringSpeed = ((ItemSteerable) impl).getSteeringSpeed();
            final int boostTime = ((Steerable) entity).getCurrentBoostTicks();
            final int boostTimeTotal = ((Steerable) entity).getBoostTicks();
            if (boostTimeTotal != 0) {
                steeringSpeed += steeringSpeed * 1.15F * Mth.sin((float) boostTime / (float) boostTimeTotal * (float) Math.PI);
            }

            impl.setSpeed(steeringSpeed);
            ((ItemSteerable) impl).travelWithInput(new Vec3(0.0D, 0.0D, 1.0D));
            entry.setValue(impl.getDeltaMovement());
        }
    }

    @Override
    public Object getVec3Zero() {
        return Vec3.ZERO;
    }
}
