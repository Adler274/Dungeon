package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;

public class TrapSwitch extends Entity{
    private final float defaultInteractionRadius = 1;
    private final String DEFAULT_UNUSED_ANIMATION_FRAME = "objects/traps/switch/lever_left.png";
    private final String DEFAULT_USED_ANIMATION_FRAME = "objects/traps/switch/lever_right.png";
    private AnimationComponent ac;
    private final Trap trap;

    public TrapSwitch(Trap trap){
        this.trap = trap;
        new PositionComponent(this);
        ac = new AnimationComponent(this, AnimationBuilder.buildAnimation(DEFAULT_UNUSED_ANIMATION_FRAME));
        new InteractionComponent(this, defaultInteractionRadius, false, this::disarmTrap);
    }

    private void disarmTrap(Entity entity){
        trap.disarm();
        ac.setCurrentAnimation(AnimationBuilder.buildAnimation(DEFAULT_USED_ANIMATION_FRAME));
    }
}
