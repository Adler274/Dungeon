package ecs.entities.npc;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.GhostWalk;
import ecs.components.ai.transition.RangeTransition;
import ecs.entities.Entity;
import graphic.Animation;

/** A freindly follower (used to trigger an effect in conjunction with a Tombstone object)*/
public class Ghost extends Entity {

    private final float xSpeed = 0.1f;
    private final float ySpeed = 0.1f;
    private final String pathToIdleLeft = "npc/ghost/idleLeft";
    private final String pathToIdleRight = "npc/ghost/idleRight";
    private final String pathToRunLeft = "npc/ghost/runLeft";
    private final String pathToRunRight = "npc/ghost/runRight";

    /** Entity with Components*/
    public Ghost(){
        super();
        new AIComponent(this, new CollideAI(0f), new GhostWalk(), new RangeTransition(0));
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
    }

    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
        Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
        new AnimationComponent(this, idleLeft, idleRight);
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
            this,
            null,
            null);
    }
}
