package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import starter.Game;

public class SlowTrap extends Trap{

    private final String DEFAULT_ANIMATION_FRAME = "animation/missingTexture.png";
    private boolean armed = true;

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
        if (armed) {
            Game.getHero().get().getComponent(VelocityComponent.class).ifPresent((vc) ->
                ((VelocityComponent) vc).setXVelocity((float) (((VelocityComponent) vc).getXVelocity() * 0.4)));
            Game.getHero().get().getComponent(VelocityComponent.class).ifPresent((vc) ->
                ((VelocityComponent) vc).setYVelocity((float) (((VelocityComponent) vc).getYVelocity() * 0.4)));
            disarm();
        }
    }

    @Override
    protected void disarm() {
        this.armed = false;
    }
}
