package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import tools.Point;

public class SwordSkill extends DamageMeleeSkill {

    public SwordSkill(ITargetSelection targetSelection) {
        super(
                "skills/sword/sword_Up/",
                "skills/sword/sword_Left/",
                "skills/sword/sword_Right/",
                "skills/sword/sword_Down/",
                new Damage(1, DamageType.PHYSICAL, null),
                new Point(10, 10),
                targetSelection,
                1f);
    }
}
