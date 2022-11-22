package net.camotoy.geyserhacks;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public final class GeyserHacksConfiguration {
    @Comment("\"Fixes\" Bedrock players running into lagback issues on certain blocks by removing any collision detection " +
            "from the given block.\n" +
            "Caveats: a custom client - Java or Bedrock - could take advantage of no collision and walk right through.\n" +
            " Additionally, placement of these blocks on both platforms may be buggier than usual.")
    private CollisionFixes collisionFixes = new CollisionFixes();
    @Comment("Fixes Bedrock players being unable to control pigs and striders by controlling their movement serverside.\n" +
            "Java Edition controls pigs and striders on the client end. Bedrock depends on the server.\n" +
            "This option should be relatively safe but does modify server behavior. Geyser or Floodgate must be installed, as well as a recent server version.")
    private boolean itemSteerableFix = true;
    @Comment("Fixes Bedrock players being unable to use any item with the sweeping edge enchant.\n"
    		+ "For Bedrock Players, this adds Unbreaking i enchant to item if no other enchants, and will change the name of the item to reflect the level of sweeping edge.\n" +
            "This option should be relatively safe but does modify server behavior. Geyser or Floodgate must be installed, as well as a recent server version.")
    private boolean sweepingEdgeFix = true;
    

    public CollisionFixes collisionFixes() {
        return collisionFixes;
    }

    public boolean itemSteerableFix() {
        return itemSteerableFix;
    }
    
    public boolean sweepingEdgeFix() {
        return sweepingEdgeFix;
    }

    @ConfigSerializable
    static final class CollisionFixes {
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
