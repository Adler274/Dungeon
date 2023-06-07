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
                AnimationBuilder.buildAnimation("ui_heart_empty.png"),  // TODO change
                AnimationBuilder.buildAnimation("ui_heart_empty.png"),  // TODO change
                "ExperiencePotion",
                "Adds XPComponent of the hero XP");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(ItemData::defaultDrop);
        this.setOnUse(this::onUse);
    }

    public void onCollect(Entity worldItem, Entity whoCollected) {
        Game.getHero()
            .ifPresent(
                hero -> {
                    if (whoCollected.equals(hero)) {
                        hero.getComponent(InventoryComponent.class)
                            .ifPresent(
                                (ic) -> {
                                    ((InventoryComponent) ic).addItem(this);
                                    Game.removeEntity(worldItem);
                                });
                    }
                });
    }

    public void onDrop(Entity user, ItemData which, Point position) {}

    public void onUse(Entity e, ItemData item) {
        // granting xp
        Game.getHero()
                .get()
                .getComponent(XPComponent.class)
                .ifPresent(
                        xc -> {
                            ((XPComponent) xc).addXP(((XPComponent) xc).getXPToNextLevel());
                        });

        // removing item
        e.getComponent(InventoryComponent.class)
                .ifPresent(ic -> ((InventoryComponent) ic).removeItem(this));
    }
}
