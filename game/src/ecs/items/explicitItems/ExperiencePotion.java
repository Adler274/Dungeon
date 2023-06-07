package ecs.items.explicitItems;

import dslToGame.AnimationBuilder;
import ecs.entities.Entity;
import ecs.items.*;
import tools.Point;

public class ExperiencePotion extends ItemData implements IOnCollect, IOnDrop, IOnUse {

    public ExperiencePotion(){
        super(
            ItemType.POTION,
            AnimationBuilder.buildAnimation("missingTexture.png"),
            AnimationBuilder.buildAnimation("missingTexture.png"),
            "ExperiencePotion",
            "Adds XPComponent of the hero XP");
        this.setOnCollect(this);
        this.setOnDrop(this);
        this.setOnUse(this);

    }

    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {

    }

    @Override
    public void onDrop(Entity user, ItemData which, Point position) {

    }

    @Override
    public void onUse(Entity e, ItemData item) {

    }
}
