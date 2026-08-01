package org.geysermc.hurricane.paper;

public final class PaperMovementDecision {
    private PaperMovementDecision() {
    }

    public static boolean shouldAllowFailedMove(FailedMoveEvaluation evaluation) {
        return evaluation.bedrockPlayer()
                && evaluation.clippedIntoBlock()
                && evaluation.javaTranslatedOverlap()
                && !evaluation.bedrockTranslatedOverlap()
                && !evaluation.otherBlockCollision()
                && !evaluation.entityCollision();
    }

    public record FailedMoveEvaluation(
            boolean bedrockPlayer,
            boolean clippedIntoBlock,
            boolean javaTranslatedOverlap,
            boolean bedrockTranslatedOverlap,
            boolean otherBlockCollision,
            boolean entityCollision
    ) {
    }
}
