package ecs.items.ItemEntities;

import dslToGame.AnimationBuilder;
import ecs.components.PositionComponent;
import ecs.entities.Entity;
import ecs.items.*;
import graphic.Animation;
import tools.Point;

public class Bag extends ItemData implements IOnCollect, IOnDrop, IOnUse {

    private final int max = 3;
    public Bag(){
        super(
            ItemType.Bag,
            AnimationBuilder.buildAnimation("missingTexture.png"),
            AnimationBuilder.buildAnimation("missingTexture.png"),
            "Bag",
            "Holds items for the hero");
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
