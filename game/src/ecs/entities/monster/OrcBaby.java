package ecs.entities.monster;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.RadiusWalk;
import ecs.components.ai.transition.SelfDefendTransition;
import ecs.entities.Entity;
import graphic.Animation;

/**
 * OrcBaby is a hostile character. It's entity in the ECS. This class helps to setup the orcBaby with
 * all its components and attributes .
 */
public class OrcBaby extends Entity {

    private final float xSpeed = 0.4f;
    private final float ySpeed = 0.4f;
    private final String pathToIdleLeft = "monster/orcBaby/idleLeft";
    private final String pathToIdleRight = "monster/orcBaby/idleRight";
    private final String pathToRunLeft = "monster/orcBaby/runLeft";
    private final String pathToRunRight = "monster/orcBaby/runRight";

    /** Entity with Components */
    public OrcBaby(){
        super();
        new AIComponent(this, new CollideAI(0f), new RadiusWalk(20, 1), new SelfDefendTransition());     // FightAI to be changed
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        setupHealthComponent();
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
            (you, other, direction) -> System.out.println("orcBabyCollisionEnter"),
            (you, other, direction) -> System.out.println("orcBabyCollisionLeave"));
    }

    private void setupHealthComponent(){
        HealthComponent hc = new HealthComponent(this);
        hc.setMaximalHealthpoints(1);
        hc.setCurrentHealthpoints(1);
    }
}
