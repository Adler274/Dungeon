package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;

public class SlowTrap extends Trap{

    private final String DEFAULT_ANIMATION_FRAME = "animation/missingTexture.png";

    public SlowTrap(){
        new PositionComponent(this);
        new AnimationComponent(this, AnimationBuilder.buildAnimation((DEFAULT_ANIMATION_FRAME)));
        new HitboxComponent(
            this,
            (you, other, direction) -> {
                System.out.println("SlowTrapCollisionEnter");
                if (other.getComponent(PlayableComponent.class).isPresent()){
                    effect();
                }
            },
            (you, other, direction) -> System.out.println("SlowTrapCollisionLeave"));
    }
    @Override
    protected void effect() {
        //Speed verringern
    }

    @Override
    protected void disarm() {}
}
