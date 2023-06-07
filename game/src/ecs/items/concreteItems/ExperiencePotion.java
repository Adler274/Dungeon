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
                "gives the user enough xp to level up");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(ItemData::defaultDrop);
        this.setOnUse(this::onUse);
    }

    /**
     * @param worldItem entity of the item
     * @param whoCollected entity which collected the item
     */
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

    /**
     * @param e entity that uses the item
     * @param item the items being used
     */
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
