package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.components.xp.XPComponent;
import ecs.entities.Entity;
import ecs.items.*;
import starter.Game;

public class ExperiencePotion extends ItemData {

    public ExperiencePotion() {
        super(
                ItemType.POTION,
                10,
                AnimationBuilder.buildAnimation("xp_potion.png"),
                AnimationBuilder.buildAnimation("xp_potion.png"),
                "ExperiencePotion",
                "gives the user enough xp to level up",
                "experience potion");  // TODO change pattern
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
                                                    if (((InventoryComponent) ic).addItem(this)) {
                                                        Game.removeEntity(worldItem);
                                                    }
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

    /**
     * @return name of the item with spaces
     */
    @Override
    public String getItemName() {
        return "Experience Potion";
    }
}
