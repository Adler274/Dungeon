package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.InteractionComponent;
import ecs.components.PositionComponent;
import ecs.components.ai.AITools;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import ecs.entities.npc.Ghost;
import starter.Game;
import java.util.concurrent.ThreadLocalRandom;

/** A none moving Entity with a method to remove most Entities*/
public class Tombstone extends Entity {

    //private final float defaultInteractionRadius = 1f;
    private final String DEFALUT_ANIMATION_FRAME = "objects/tombstone/weapon_rusty_sword.png";

    /** Is here so that the tombstone can check the distance between itself and the ghost*/
    private Ghost ghost;


    /** Entity with Componenets and a Ghost*/
    public Tombstone(Ghost ghost) {
        this.ghost = ghost;
        new PositionComponent(this);
        //new InteractionComponent(this, defaultInteractionRadius, false, this::spawnRandoMonster);
        new AnimationComponent(this, AnimationBuilder.buildAnimation((DEFALUT_ANIMATION_FRAME)));
    }

    /*
    private void spawnRandoMonster(Entity tombstone) {
        if (AITools.entityInRange(Game.getHero().get(), ghost, 3f)){
            Game.removeEntity(ghost);
            int rando = ThreadLocalRandom.current().nextInt(0, 3);
            if (rando == 0){
                Game.addEntity(new OrcNormal());
            } else if (rando == 1){
                Game.addEntity(new OrcBaby());
            } else{
                Game.addEntity(new OrcMasked());
            }
        }
    }
     */

    /** Despawns most Entities if ghost and hero enough*/
    public void despawnAllMonsters(){
        if (AITools.entityInRange(Game.getHero().get(), this, 1f)
            && AITools.entityInRange(this, ghost, 2f)) {
            for (Entity entity : Game.getEntities()) {
                if (!(entity == Game.getHero().get() || entity == this)) {
                    Game.removeEntity(entity);
                }
            }
        }
    }
}
