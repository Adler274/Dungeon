package ecs.components.ai.idle;

import com.badlogic.gdx.ai.pfa.GraphPath;
import ecs.components.MissingComponentException;
import ecs.components.PositionComponent;
import ecs.components.ai.AITools;
import ecs.entities.Entity;
import java.util.concurrent.ThreadLocalRandom;
import level.elements.tile.Tile;
import starter.Game;

/** Specifies how Ghosts should move */
public class GhostWalk implements IIdleAI {

    private GraphPath<Tile> path;
    /** Tells you if the current movement is towards a random spot */
    private boolean randomPath = false;

    /**
     * Randomly does one of the following things: <br>
     * 95% following the hero <br>
     * 4.7% moving towards a random spot in the room <br>
     * 0.3% getting deleted
     */
    @Override
    public void idle(Entity entity) {
        int rando = ThreadLocalRandom.current().nextInt(0, 1000);
        if (!randomPath) {
            if (rando < 950) {
                path = AITools.calculatePathToHero(entity);
            } else {
                randomPath = true;
                PositionComponent position =
                        (PositionComponent)
                                entity.getComponent(PositionComponent.class)
                                        .orElseThrow(
                                                () ->
                                                        new MissingComponentException(
                                                                "PositionComponent"));
                path =
                        AITools.calculatePathToRandomTileInRange(
                                position.getPosition().toCoordinate().toPoint(), 50f);
            }
        }
        if (rando > 997) {
            Game.removeEntity(entity);
        }
        if (AITools.pathFinished(entity, path)) {
            randomPath = false;
        }
        AITools.move(entity, path);
    }
}
