package org.geysermc.hurricane.v1_20_R1;

import org.geysermc.hurricane.NMSProvider;
import org.geysermc.hurricane.NMSReflection;
import org.geysermc.hurricane.ReflectionAPI;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftAnimals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;

import java.lang.reflect.Method;
import java.util.Map;

public class NMSProviderImpl implements NMSProvider {
    private static Method tickRiddenMethod;

    static {
        try {
            tickRiddenMethod = ReflectionAPI.getMethod(
                NMSReflection.getMojmapNMSClass("world.entity.animal.EntityPig"), 
                "a", 
                NMSReflection.getMojmapNMSClass("world.entity.player.EntityHuman"),
                NMSReflection.getMojmapNMSClass("world.phys.Vec3D"));
                
            tickRiddenMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forEachPlayerOnSteerable(Entity entity, Map.Entry<Player, Object> entry) {
        final Mob impl = ((CraftAnimals) entity).getHandle();

        // Replicate ItemSteerable
        if (impl.isVehicle() && impl.hasControllingPassenger() && impl instanceof Pig) {
            // Used to mitigate the server setting delta movement to zero
            impl.setDeltaMovement((Vec3) entry.getValue());
            float steeringSpeed = (float) ((float) ((Pig) impl).getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225 * ((Pig) impl).steering.boostFactor());
            final int boostTime = ((Steerable) entity).getCurrentBoostTicks();
            final int boostTimeTotal = ((Steerable) entity).getBoostTicks();
            if (boostTimeTotal != 0) {
                steeringSpeed += steeringSpeed * 1.15F * Mth.sin((float) boostTime / (float) boostTimeTotal * (float) Math.PI);
            }

            impl.setSpeed(steeringSpeed);
            
            // Truly cursed
            net.minecraft.world.entity.player.Player controllingPassenger = (net.minecraft.world.entity.player.Player) impl.getControllingPassenger();
            ReflectionAPI.invokeMethod((Pig) impl, tickRiddenMethod, controllingPassenger, new Vec3(0.0D, 0.0D, 1.0D));
            
            entry.setValue(impl.getDeltaMovement());
        }
    }

    @Override
    public Object getVec3Zero() {
        return Vec3.ZERO;
    }
}
