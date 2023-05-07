package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import tools.Point;

public class PiercingArrowSkill extends DamageProjectileSkill {

    public PiercingArrowSkill(ITargetSelection targetSelection) {
        super(
                "animation/missingTexture.png",
                0.5f,
                new Damage(1, DamageType.PHYSICAL, null),
                new Point(10, 10),
                targetSelection,
                5f,
                0f,
                true);
    }
}
