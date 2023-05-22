package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.entities.Entity;
import graphic.Animation;
import tools.Point;

public abstract class NonDamagingProjectilleSpells implements ISkillFunction {

    protected String pathToTexturesOfProjectile;
    protected float projectileSpeed;
    protected float projectileRange;
    protected Point projectileHitboxSize;
    protected ITargetSelection selectionFunction;
    protected int healthCost;

    public NonDamagingProjectilleSpells(
            String pathToTexturesOfProjectile,
            float projectileSpeed,
            int healthCost,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction,
            float projectileRange) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.healthCost = healthCost;
        this.selectionFunction = selectionFunction;
    }

    @Override
    public void execute(Entity entity) {
        entity.getComponent(HealthComponent.class)
                .ifPresent(
                        hc -> {
                            if (((HealthComponent) hc).getCurrentHealthpoints() > healthCost) {
                                ((HealthComponent) hc)
                                        .setCurrentHealthpoints(
                                                ((HealthComponent) hc).getCurrentHealthpoints()
                                                        - healthCost);
                                Entity projectile = new Entity();
                                PositionComponent epc =
                                        (PositionComponent)
                                                entity.getComponent(PositionComponent.class)
                                                        .orElseThrow(
                                                                () ->
                                                                        new MissingComponentException(
                                                                                "PositionComponent"));
                                new PositionComponent(projectile, epc.getPosition());

                                Animation animation =
                                        AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
                                new AnimationComponent(projectile, animation);
                                Point aimedOn = selectionFunction.selectTargetPoint();
                                Point targetPoint =
                                        SkillTools.calculateLastPositionInRange(
                                                epc.getPosition(), aimedOn, projectileRange);
                                Point velocity =
                                        SkillTools.calculateVelocity(
                                                epc.getPosition(), targetPoint, projectileSpeed);
                                new VelocityComponent(
                                        projectile, velocity.x, velocity.y, animation, animation);
                                new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
                                this.effect(entity, projectile);
                            }
                        });
    }

    protected abstract void effect(Entity entity, Entity projectile);
}
