package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import starter.Game;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnerTrap extends Trap{

    private final String DEFAULT_ANIMATION_FRAME = "animation/missingTexture.png";
    private boolean armed = true;

    public SpawnerTrap(){
        new PositionComponent(this);
        new AnimationComponent(this, AnimationBuilder.buildAnimation((DEFAULT_ANIMATION_FRAME)));
        new HitboxComponent(
            this,
            (you, other, direction) -> {
                System.out.println("SpawnerTrapCollisionEnter");
                if (other.getComponent(PlayableComponent.class).isPresent()){
                    effect();
                }
            }, (you, other, direction) -> System.out.println("SpawnerTrapCollisionLeave"));
    }

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
            armed = false;
        }
    }

    @Override
    public void disarm() {
        this.armed = false;
    }
}
