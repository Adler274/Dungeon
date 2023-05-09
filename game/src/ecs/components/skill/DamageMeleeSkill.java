package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Constants;
import tools.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DamageMeleeSkill implements ISkillFunction {

    private String pathToTexturesOfAttackUp;
    private String pathToTexturesOfAttackLeft;
    private String pathToTexturesOfAttackRight;
    private String pathToTexturesOfAttackDown;
    private Damage attackDamage;
    private Point attackHitboxSize;
    private ITargetSelection selectionFunction;
    private float knockback;

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
        attack = new Entity();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        Point aimedOn = selectionFunction.selectTargetPoint();
        String direction = SkillTools.calculateMeleeDirection(
            epc.getPosition(), aimedOn);
        Animation animation = AnimationBuilder.buildAnimation("animation/missingTexture.png");
        Point targetPoint = epc.getPosition();
        switch (direction){
            case "up" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackUp);
                targetPoint = new Point(epc.getPosition().x, epc.getPosition().y+0.5f);
            }
            case "down" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackDown);
                targetPoint = new Point(epc.getPosition().x, epc.getPosition().y-0.75f);
            }
            case "left" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackLeft);
                targetPoint = new Point(epc.getPosition().x-0.75f, epc.getPosition().y);
            }
            case "right" -> {
                animation = AnimationBuilder.buildAnimation(pathToTexturesOfAttackRight);
                targetPoint = new Point(epc.getPosition().x+0.5f, epc.getPosition().y);
            }
        }
        Animation animation1 = new Animation(List.of(
            "skills/sword/sword_Up/sword_Up0.png",
            "skills/sword/sword_Up/sword_Up1.png",
            "skills/sword/sword_Up/sword_Up2.png"),
            5, false);
        AnimationComponent ac = new AnimationComponent(attack, animation1);
        new PositionComponent(attack, targetPoint);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc).receiveHit(attackDamage);
                                            SkillTools.applyKnockback(epc.getPosition(),b ,knockback);
                                        });
                    }
                };
        new HitboxComponent(
                attack, new Point(0.25f, 0.25f), attackHitboxSize, collide, null);

            if (ac.getCurrentAnimation().isFinished()){
                Game.removeEntity(attack);
        }
    }
    public static void updateAttackAnimation() {
        if (Game.getUpdateSwordAttack() >= 10) {
            for (Entity a : Game.getEntities()) {
                System.out.println("hier:" + Game.getUpdateSwordAttack());
                if (a == attack) {
                    Game.removeEntity(attack);
                }
                Game.setUpdateSwordAttack(0f);
            }

        }
    }
}
