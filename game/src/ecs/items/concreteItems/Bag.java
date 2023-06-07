package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.*;
import java.util.ArrayList;
import java.util.List;
import starter.Game;
import tools.Point;

public class Bag extends ItemData {

    private List<ItemData> inventory;
    private final int max = 3;

    public Bag() {
        super(
                ItemType.BAG,
                AnimationBuilder.buildAnimation("bag.png"),
                AnimationBuilder.buildAnimation("bag.png"),
                "Bag",
                "Holds items of one type for the hero");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(ItemData::defaultDrop);
        this.setOnUse(this::onUse);

        WorldItemBuilder.buildWorldItem(this);
        inventory = new ArrayList<>(3);
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

    public boolean addItem(ItemData itemData) {
        if (inventory.size() >= max) {
            return false;
        }
        return inventory.add(itemData);
    }

    public void onDrop(Entity user, ItemData which, Point position) {}

    public void onUse(Entity e, ItemData item) {
        System.out.println("Bag used");
    }
}
