package ecs.entities.monster;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.AtoBWalk;
import ecs.components.ai.transition.RangeTransition;
import ecs.components.stats.StatsComponent;
import ecs.components.xp.XPComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;

/**
 * OrcMasked is a hostile character. It's entity in the ECS. This class helps to setup the orcMasked
 * with all its components and attributes .
 */
public class OrcMasked extends Entity {

    private final float xSpeed = 0.1f;
    private final float ySpeed = 0.1f;
    private final int health = 5;
    private final int lootXP = 25;
    private final String pathToIdleLeft = "monster/orcMasked/idleLeft";
    private final String pathToIdleRight = "monster/orcMasked/idleRight";
    private final String pathToRunLeft = "monster/orcMasked/runLeft";
    private final String pathToRunRight = "monster/orcMasked/runRight";

    /** Entity with Components */
    public OrcMasked() {
        super();
        new AIComponent(this, new CollideAI(2f), new AtoBWalk(), new RangeTransition(3));
        new PositionComponent(this);
        setupVelocityComponent();
        setupAnimationComponent();
        setupHitboxComponent();
        setupHealthComponent();
        setupXpComponent();
        new StatsComponent(this);
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

    private void setupXpComponent() {
        XPComponent xc = new XPComponent(this);
        xc.setLootXP(lootXP);
    }

    /** Setting up HitboxComponent to deal damage to player when colliding */
    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> {
                    if (other.getComponent(PlayableComponent.class).isPresent()) {
                        other.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc ->
                                                ((HealthComponent) hc)
                                                        .receiveHit(
                                                                new Damage(
                                                                        3,
                                                                        DamageType.PHYSICAL,
                                                                        this)));
                    }
                },
                null);
    }

    private void setupHealthComponent() {
        Animation hcAnimation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
        HealthComponent hc =
                new HealthComponent(this, health, this::onDeath, hcAnimation, hcAnimation);
        hc.setMaximalHealthpoints(health);
        hc.setCurrentHealthpoints(health);
    }

    private void onDeath(Entity entity) {
        Game.getHero()
                .get()
                .getComponent(XPComponent.class)
                .ifPresent(
                        xc -> {
                            ((XPComponent) xc).addXP(lootXP);
                        });
    }
}
