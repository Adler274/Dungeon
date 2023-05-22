package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.components.stats.StatsComponent;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Point;

public class PhysicalWeaknessSpell extends NonDamagingProjectilleSpells {
//TODO Eigene Animation fuer den PhysicalWeaknessSpell (zurzeit noch Feuerball)

    public PhysicalWeaknessSpell(ITargetSelection targetSelection) {
        super(
            "skills/fireball/fireBall_Down/",
            0.5f,
            1,
            new Point(10, 10),
            targetSelection,
            5f);
    }

    @Override
    protected void effect(Entity entity, Entity projectile) {
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
    }
}
