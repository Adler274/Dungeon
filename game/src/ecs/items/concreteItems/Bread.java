package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.entities.Entity;
import ecs.items.*;
import tools.Point;

public class Bread extends ItemData {

    public Bread() {
        super(
                ItemType.FOOD,
                AnimationBuilder.buildAnimation("missingTexture.png"),
                AnimationBuilder.buildAnimation("missingTexture.png"),
                "Bread",
                "Heals the hero by 5 health points");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(this::onDrop);
        this.setOnUse(this::onUse);
    }

    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {}

    public void onDrop(Entity user, ItemData which, Point position) {}

    public void onUse(Entity e, ItemData item) {}
}
