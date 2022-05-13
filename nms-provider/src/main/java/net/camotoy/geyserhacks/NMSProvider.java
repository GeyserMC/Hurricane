package net.camotoy.geyserhacks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public interface NMSProvider {
    void forEachPlayerOnSteerable(Entity entity, Map.Entry<Player, Object> entry);

    Object getVec3Zero();
}
