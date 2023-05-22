package ecs.entities.traps;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.entities.Entity;

/** switch used to disarm connected trap */
public class TrapSwitch extends Entity {
    private final float defaultInteractionRadius = 1;
    private final String DEFAULT_UNUSED_ANIMATION_FRAME = "objects/traps/switch/lever_left.png";
    private final String DEFAULT_USED_ANIMATION_FRAME = "objects/traps/switch/lever_right.png";
    private AnimationComponent ac;
    /** trap which is controlled by this switch*/
    private final Trap trap;

    /**
     * Entity with Components
     */
    public TrapSwitch(Trap trap){
        this.trap = trap;
        new PositionComponent(this);
        ac = new AnimationComponent(this, AnimationBuilder.buildAnimation(DEFAULT_UNUSED_ANIMATION_FRAME));
        new InteractionComponent(this, defaultInteractionRadius, false, this::disarmTrap);
    }

    /**
     * disarms the trap
     */
    private void disarmTrap(Entity entity){
        trap.disarm();
        ac.setCurrentAnimation(AnimationBuilder.buildAnimation(DEFAULT_USED_ANIMATION_FRAME));
    }
}
