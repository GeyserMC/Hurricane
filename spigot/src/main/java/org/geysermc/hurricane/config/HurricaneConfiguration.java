package org.geysermc.hurricane.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public final class HurricaneConfiguration {
    @Comment("\"Fixes\" Bedrock players running into lagback issues on certain blocks by removing any collision detection " +
            "from the given block.\n" +
            "Caveats: a custom client - Java or Bedrock - could take advantage of no collision and walk right through.\n" +
            " Additionally, placement of these blocks on both platforms may be buggier than usual.")
    private CollisionFixes collisionFixes = new CollisionFixes();

    @Comment("The version of the config. DO NOT CHANGE!")
    private int version = 1;

    public CollisionFixes collisionFixes() {
        return collisionFixes;
    }

    @ConfigSerializable
    public static final class CollisionFixes {
        private boolean bamboo = true;
        private boolean pointedDripstone = true;

        public boolean bamboo() {
            return bamboo;
        }

        public boolean pointedDripstone() {
            return pointedDripstone;
        }
    }
}
