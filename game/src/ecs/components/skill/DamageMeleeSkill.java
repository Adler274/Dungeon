package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Point;

public abstract class DamageMeleeSkill implements ISkillFunction {

    private String pathToTexturesOfAttackUp;
    private String pathToTexturesOfAttackLeft;
    private String pathToTexturesOfAttackRight;
    private String pathToTexturesOfAttackDown;
    private Damage attackDamage;
    private Point attackHitboxSize;
    private ITargetSelection selectionFunction;
    private float knockback;

    /** counts how long the current animation is running */
    private static int animationFrames;
    /** shows if an attack is currently happening */
    private static boolean isActive;
    /** current attack */
    private static Entity attack;

    public DamageMeleeSkill(
            String pathToTexturesOfAttackUp,
            String pathToTexturesOfAttackLeft,
            String pathToTexturesOfAttackRight,
            String pathToTexturesOfAttackDown,
            Damage attackDamage,
            Point attackHitboxSize,
            ITargetSelection selectionFunction,
            float knockback) {
        this.pathToTexturesOfAttackUp = pathToTexturesOfAttackUp;
        this.pathToTexturesOfAttackLeft = pathToTexturesOfAttackLeft;
        this.pathToTexturesOfAttackRight = pathToTexturesOfAttackRight;
        this.pathToTexturesOfAttackDown = pathToTexturesOfAttackDown;
        this.attackDamage = attackDamage;
        this.attackHitboxSize = attackHitboxSize;
        this.selectionFunction = selectionFunction;
        this.knockback = knockback;
    }

    @Override
    public void execute(Entity entity) {
        isActive = true;
        attack = new Entity();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        Point aimedOn = selectionFunction.selectTargetPoint();
        String direction = SkillTools.calculateMeleeDirection(epc.getPosition(), aimedOn);
        Animation animation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
        Point targetPoint = epc.getPosition();
        switch (direction) {
            case "up" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackUp);
                targetPoint = new Point(epc.getPosition().x, epc.getPosition().y + 0.5f);
            }
            case "down" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackDown);
                targetPoint = new Point(epc.getPosition().x, epc.getPosition().y - 0.75f);
            }
            case "left" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackLeft);
                targetPoint = new Point(epc.getPosition().x - 0.75f, epc.getPosition().y);
            }
            case "right" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackRight);
                targetPoint = new Point(epc.getPosition().x + 0.5f, epc.getPosition().y);
            }
        }
        new AnimationComponent(attack, animation);
        new PositionComponent(attack, targetPoint);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc).receiveHit(attackDamage);
                                            SkillTools.applyKnockback(
                                                    epc.getPosition(), b, knockback);
                                        });
                    }
                };
        new HitboxComponent(attack, new Point(0.25f, 0.25f), attackHitboxSize, collide, null);
    }

    /** counts frames and removes attack upon completion */
    public static void update() {
        if (isActive) {
            if (animationFrames >= 16) {
                for (Entity a : Game.getEntities()) {
                    if (a == attack) {
                        Game.removeEntity(attack);
                    }
                    animationFrames = 0;
                    isActive = false;
                }
            }
            animationFrames++;
        }
    }
}
