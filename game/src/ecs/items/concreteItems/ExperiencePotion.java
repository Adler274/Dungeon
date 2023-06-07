package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.components.xp.XPComponent;
import ecs.entities.Entity;
import ecs.items.*;
import starter.Game;
import tools.Point;

public class ExperiencePotion extends ItemData {

    public ExperiencePotion() {
        super(
                ItemType.POTION,
                AnimationBuilder.buildAnimation("missingTexture.png"),
                AnimationBuilder.buildAnimation("missingTexture.png"),
                "ExperiencePotion",
                "Adds XPComponent of the hero XP");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(this::onDrop);
        this.setOnUse(this::onUse);
    }

    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {}

    public void onDrop(Entity user, ItemData which, Point position) {}

    public void onUse(Entity e, ItemData item) {
        //granting xp
        Game.getHero()
            .get()
            .getComponent(XPComponent.class)
            .ifPresent(
                xc -> {
                    ((XPComponent) xc).addXP(((XPComponent) xc).getXPToNextLevel());
                });

        //removing item
        e.getComponent(InventoryComponent.class)
            .ifPresent(
                ic -> ((InventoryComponent) ic).removeItem(this));
    }
}
