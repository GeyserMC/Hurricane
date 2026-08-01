package org.geysermc.hurricane.paper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PaperMovementDecisionTest {
    @Test
    void allowsMoveRejectedOnlyByJavaTranslatedCollisionForBedrockPlayer() {
        assertTrue(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, true, true, false, false, false
        )));
    }

    @Test
    void refusesOverrideForJavaPlayerOrDifferentFailureReason() {
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                false, true, true, false, false, false
        )));
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, false, true, false, false, false
        )));
    }

    @Test
    void refusesOverrideWithoutTranslatedCollisionMismatch() {
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, true, false, false, false, false
        )));
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, true, true, true, false, false
        )));
    }

    @Test
    void preservesOtherBlockAndEntityCollision() {
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, true, true, false, true, false
        )));
        assertFalse(PaperMovementDecision.shouldAllowFailedMove(evaluation(
                true, true, true, false, false, true
        )));
    }

    private static PaperMovementDecision.FailedMoveEvaluation evaluation(
            boolean bedrockPlayer,
            boolean clippedIntoBlock,
            boolean javaTranslatedOverlap,
            boolean bedrockTranslatedOverlap,
            boolean otherBlockCollision,
            boolean entityCollision
    ) {
        return new PaperMovementDecision.FailedMoveEvaluation(
                bedrockPlayer,
                clippedIntoBlock,
                javaTranslatedOverlap,
                bedrockTranslatedOverlap,
                otherBlockCollision,
                entityCollision
        );
    }
}
