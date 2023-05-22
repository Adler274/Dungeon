package ecs.entities.traps;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import starter.Game;

import java.util.concurrent.ThreadLocalRandom;

/** A trap spawning a random monster on collision */
public class SpawnerTrap extends Trap {

    private final String DEFAULT_UNUSED_ANIMATION_FRAME = "objects/traps/spawner/button_red_up.png";
    private final String DEFAULT_USED_ANIMATION_FRAME = "objects/traps/spawner/button_red_down.png";
    private boolean armed = true;
    private AnimationComponent ac;

    /**
     * Entity with Components
     */
    public SpawnerTrap(){
        new PositionComponent(this);
        ac = new AnimationComponent(this, AnimationBuilder.buildAnimation((DEFAULT_UNUSED_ANIMATION_FRAME)));
        new HitboxComponent(
            this,
            (you, other, direction) -> {
                System.out.println("SpawnerTrapCollisionEnter");
                if (other.getComponent(PlayableComponent.class).isPresent()){
                    effect();
                }
            }, (you, other, direction) -> System.out.println("SpawnerTrapCollisionLeave"));
    }

    /**
     * spawns a random monster and disarms the trap after use
     */
    @Override
    public void effect() {
        if (armed){
            int rando = ThreadLocalRandom.current().nextInt(0, 3);
            if (rando == 0){
                Game.addEntity(new OrcNormal());
            } else if (rando == 1){
                Game.addEntity(new OrcBaby());
            } else{
                Game.addEntity(new OrcMasked());
            }
            disarm();
        }
    }

    /**
     * disarms the trap
     */
    @Override
    public void disarm() {
        this.armed = false;
        ac.setCurrentAnimation(AnimationBuilder.buildAnimation(DEFAULT_USED_ANIMATION_FRAME));
    }
}
