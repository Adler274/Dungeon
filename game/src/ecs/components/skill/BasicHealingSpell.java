package ecs.components.skill;

import ecs.components.HealthComponent;
import ecs.components.MissingComponentException;
import ecs.entities.Entity;
import java.util.logging.Logger;
import logging.CustomLogLevel;

public class BasicHealingSpell implements ISkillFunction {
    private int healingPotency;
    private final Logger spellLogger = Logger.getLogger(this.getClass().getSimpleName());

    public BasicHealingSpell(int healingPotency) {
        this.healingPotency = healingPotency;
    }

    /** Increases the users current health by healingPotency */
    public void execute(Entity entity) {
        HealthComponent hc =
                (HealthComponent)
                        entity.getComponent(HealthComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("HealthComponent"));
        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() + healingPotency);

        spellLogger.log(
                CustomLogLevel.INFO,
                this.getClass().getSimpleName()
                        + " was used by '"
                        + entity.getClass().getSimpleName()
                        + "', new HP: "
                        + hc.getCurrentHealthpoints());
    }
}
