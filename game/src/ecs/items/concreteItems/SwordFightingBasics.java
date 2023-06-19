package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.*;
import starter.Game;

public class SwordFightingBasics extends ItemData {

    public SwordFightingBasics() {
        super(
                ItemType.BOOK,
                20,
                AnimationBuilder.buildAnimation("swordskill_book.png"),
                AnimationBuilder.buildAnimation("swordskill_book.png"),
                "SwordFightingBasics",
                "if used by the hero, he learns how to use a sword",
                "sword fighting basics");  // TODO change pattern
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
        // learning skill if used by hero
        if (e == Game.getHero().get()) {
            Hero hero = (Hero) Game.getHero().get();
            hero.learnSwordSkill();
        }

        // removing item
        e.getComponent(InventoryComponent.class)
                .ifPresent(ic -> ((InventoryComponent) ic).removeItem(this));
    }

    /**
     * @return name of the item with spaces
     */
    @Override
    public String getItemName() {
        return "Sword Fighting Basics";
    }
}
