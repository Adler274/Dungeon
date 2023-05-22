package ecs.components.skill;

import ecs.components.HealthComponent;
import ecs.components.MissingComponentException;
import ecs.entities.Entity;

public class BasicHealingSpell implements ISkillFunction  {
    private int healingPotency;

    public BasicHealingSpell(int healingPotency){
        this.healingPotency = healingPotency;
    }

    public void execute(Entity entity){
        HealthComponent hc =
            (HealthComponent)
                entity.getComponent(HealthComponent.class)
                    .orElseThrow(
                        () -> new MissingComponentException("HealthComponent"));
        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints()+healingPotency);
    }
}
