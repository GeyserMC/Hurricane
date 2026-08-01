package org.geysermc.hurricane.paper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.util.BoundingBox;
import org.geysermc.hurricane.core.math.Aabb;
import org.junit.jupiter.api.Test;

class PaperBoxesTest {
    @Test
    void convertsBukkitBoxToCoreWithoutChangingCoordinates() {
        BoundingBox paper = new BoundingBox(-2.25, 0.5, 4.75, 3.5, 2.3, 9.0);

        assertEquals(
                new Aabb(-2.25, 0.5, 4.75, 3.5, 2.3, 9.0),
                PaperBoxes.toCore(paper)
        );
    }

    @Test
    void roundTripsCoreBoxWithoutChangingCoordinates() {
        Aabb core = new Aabb(-2.25, 0.5, 4.75, 3.5, 2.3, 9.0);

        assertEquals(
                new BoundingBox(-2.25, 0.5, 4.75, 3.5, 2.3, 9.0),
                PaperBoxes.toPaper(PaperBoxes.toCore(PaperBoxes.toPaper(core)))
        );
    }

    @Test
    void translatesCurrentPlayerBoxToPacketPosition() {
        BoundingBox current = new BoundingBox(9.7, 64.0, 19.7, 10.3, 65.8, 20.3);

        assertEquals(
                new Aabb(10.95, 63.5, 17.7, 11.55, 65.3, 18.3),
                PaperBoxes.atPosition(
                        current,
                        10.0,
                        64.0,
                        20.0,
                        11.25,
                        63.5,
                        18.0
                )
        );
    }

    @Test
    void deflatesEveryFaceEqually() {
        Aabb box = new Aabb(0.0, 1.0, 2.0, 3.0, 5.0, 7.0);

        assertEquals(
                new Aabb(0.01, 1.01, 2.01, 2.99, 4.99, 6.99),
                PaperBoxes.deflate(box, 0.01)
        );
    }
}
