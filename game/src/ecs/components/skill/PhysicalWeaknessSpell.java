package ecs.components.skill;

import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.components.stats.StatsComponent;
import ecs.damage.DamageType;
import ecs.entities.Entity;
import starter.Game;
import tools.Point;

public class PhysicalWeaknessSpell extends NonDamagingProjectilleSpells {

    public PhysicalWeaknessSpell(ITargetSelection targetSelection) {
        super("physicalWeakness.png", 0.5f, 1, new Point(10, 10), targetSelection, 5f);
    }

    @Override
    protected void effect(Entity entity, Entity projectile) {
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(StatsComponent.class)
                                .ifPresent(
                                        sc -> {
                                            ((StatsComponent) sc)
                                                    .getDamageModifiers()
                                                    .setMultiplier(DamageType.PHYSICAL, 3);
                                            Game.removeEntity(projectile);
                                        });
                    }
                };
        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }
}
