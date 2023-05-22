package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.components.stats.StatsComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.entities.Entity;
import ecs.systems.HealthSystem;
import graphic.Animation;
import starter.Game;
import tools.Point;

public abstract class NonDamagingProjectilleSpells implements ISkillFunction {

    protected String pathToTexturesOfProjectile;
    protected float projectileSpeed;
    protected float projectileRange;
    protected Point projectileHitboxSize;
    protected ITargetSelection selectionFunction;
    protected int healthCost;
    protected boolean selfDamage = true;


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
        this.healthCost=healthCost;
        this.selectionFunction = selectionFunction;
    }

    @Override
    public void execute(Entity entity) {
        if (selfDamage ) {
        Entity projectile = new Entity();
        PositionComponent epc =
            (PositionComponent)
                entity.getComponent(PositionComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);
        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
            SkillTools.calculateLastPositionInRange(
                epc.getPosition(), aimedOn, projectileRange);
        Point velocity =
            SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, epc.getPosition(), targetPoint);

        this.effect(entity, projectile);
        /*
            ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(StatsComponent.class)
                            .ifPresent(
                                sc -> {
                                    ((StatsComponent) sc).getDamageModifiers().setMultiplier(DamageType.PHYSICAL, 3);
                                    Game.removeEntity(projectile);
                                }
                            );
                        // Leben werden nur abgezogen wenn, eine Entitiy mit StatsComponent getroffen wird.
                        Game.getHero().get().getComponent(HealthComponent.class).ifPresent(
                            hc -> {
                                if (((HealthComponent) hc).getCurrentHealthpoints() > 1) {
                                    ((HealthComponent) hc).setCurrentHealthpoints(((HealthComponent) hc).getCurrentHealthpoints() - healthCost);
                                } else {
                                    selfDamage = false;
                                }
                            }
                        );
                    }
                };
            new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
         */
        }
    }

    protected abstract void effect(Entity entity, Entity projectile);
}
