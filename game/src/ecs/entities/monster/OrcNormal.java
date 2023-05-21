package ecs.entities.monster;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.idle.PatrouilleWalk;
import ecs.components.ai.transition.SelfDefendTransition;
import ecs.components.stats.StatsComponent;
import ecs.components.xp.XPComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;

/**
 * OrcNormal is a hostile character. It's entity in the ECS. This class helps to setup the orcNormal with
 * all its components and attributes .
 */
public class OrcNormal extends Entity {

    private final float xSpeed = 0.2f;
    private final float ySpeed = 0.2f;
    private final int health = 3;
    private final String pathToIdleLeft = "monster/orcNormal/idleLeft";
    private final String pathToIdleRight = "monster/orcNormal/idleRight";
    private final String pathToRunLeft = "monster/orcNormal/runLeft";
    private final String pathToRunRight = "monster/orcNormal/runRight";
    private final int lootXP = 10;
    /**
     * Entity with Components
     */
    public OrcNormal() {
        super();
        new AIComponent(this, new CollideAI(5f), new PatrouilleWalk(20f, 4, 2000, PatrouilleWalk.MODE.RANDOM), new SelfDefendTransition());
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

    /**
     * Setting up HitboxComponent to deal damage to player when colliding
     */
    private void setupHitboxComponent() {
        new HitboxComponent(
            this,
            (you, other, direction) -> {
                if (other.getComponent(PlayableComponent.class).isPresent()){
                    other.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> (
                                (HealthComponent) hc).receiveHit(
                                    new Damage(1, DamageType.PHYSICAL, this))
                        );
                }
            },
            null);
    }

    private void setupHealthComponent() {
        Animation die=AnimationBuilder.buildAnimation("monster/orcNormal/idleLeft");
        HealthComponent hc = new HealthComponent(this,health,this::onDeath,die,die);
        hc.setMaximalHealthpoints(health);
        hc.setCurrentHealthpoints(health);
    }

    private void setupXpComponent(){
        XPComponent xc = new XPComponent(this);
        xc.setLootXP(lootXP);
    }

    private void onDeath(Entity entity){
        Game.getHero().get().getComponent(XPComponent.class).ifPresent(
            xc -> {
                ((XPComponent) xc).addXP(lootXP);
            }
        );
    }
}
