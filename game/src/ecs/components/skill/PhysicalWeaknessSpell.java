package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
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
}
