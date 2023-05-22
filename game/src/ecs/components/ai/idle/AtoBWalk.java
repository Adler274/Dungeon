package ecs.components.ai.idle;

import com.badlogic.gdx.ai.pfa.GraphPath;
import ecs.components.MissingComponentException;
import ecs.components.PositionComponent;
import ecs.components.ai.AITools;
import ecs.entities.Entity;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import level.elements.tile.Tile;
import starter.Game;

/** Movement between spawn point and another random point in th room (which is set) */
public class AtoBWalk implements IIdleAI {

    /** Current path (either pathAB or pathBA) */
    private GraphPath<Tile> path;
    /** Spawnpoint */
    private Tile posA;
    /** Random point */
    private Tile posB;

    /** Path from A to B */
    private GraphPath<Tile> pathAB;
    /** Path from B to A */
    private GraphPath<Tile> pathBA;
    /** Tells you if the current path equals pathAB */
    private boolean ab = true;

    /** Alternates moving from A to B and moving from B to A */
    @Override
    public void idle(Entity entity) {
        if (posA == null && posB == null) {
            PositionComponent position =
                    (PositionComponent)
                            entity.getComponent(PositionComponent.class)
                                    .orElseThrow(
                                            () ->
                                                    new MissingComponentException(
                                                            "PositionComponent"));
            posA = Game.currentLevel.getTileAt(position.getPosition().toCoordinate());

            List<Tile> select = AITools.getAccessibleTilesInRange(posA.getCoordinateAsPoint(), 10);
            posB = select.get(ThreadLocalRandom.current().nextInt(0, select.size()));

            pathAB =
                    AITools.calculatePath(posA.getCoordinateAsPoint(), posB.getCoordinateAsPoint());
            pathBA =
                    AITools.calculatePath(posB.getCoordinateAsPoint(), posA.getCoordinateAsPoint());
            path = pathAB;
        }

        if (AITools.pathFinished(entity, path)) {
            if (ab) {
                ab = false;
                path = pathBA;
            } else {
                ab = true;
                path = pathAB;
            }
        } else if (AITools.pathLeft(entity, path)) {
            if (ab) {
                PositionComponent position =
                        (PositionComponent)
                                entity.getComponent(PositionComponent.class)
                                        .orElseThrow(
                                                () ->
                                                        new MissingComponentException(
                                                                "PositionComponent"));
                path = AITools.calculatePath(position.getPosition(), posB.getCoordinateAsPoint());
            }
        } else AITools.move(entity, path);
    }
}
