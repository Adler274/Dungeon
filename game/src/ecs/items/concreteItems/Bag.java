package ecs.items.concreteItems;

import static ecs.items.ItemType.BAG;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.entities.Entity;
import ecs.items.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import starter.Game;

public class Bag extends ItemData {

    private List<ItemData> inventory;
    private final int inventorySize = 3;
    private ItemType inventoryType;
    private Entity bagEntity;

    public Bag() {
        super(
                BAG,
                15,
                AnimationBuilder.buildAnimation("bag.png"),
                AnimationBuilder.buildAnimation("bag.png"),
                "Bag",
                "Holds items of one type for the hero");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(ItemData::defaultDrop);
        this.setOnUse(this::onUse);

        // generating random inventoryType
        Random rand = new Random();
        while (true) {
            int ran = rand.nextInt(ItemType.values().length);
            ItemType randomType = ItemType.values()[ran];
            if (randomType != ItemType.BAG && randomType != ItemType.DEBUG) {
                inventoryType = randomType;
                break;
            }
        }
        inventory = new ArrayList<>(inventorySize);

        bagEntity = new Entity();
        bagEntity.addComponent(new InventoryComponent(bagEntity, inventorySize));
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
     * switches own inventory with that of the hero
     *
     * @param e entity using the item (not used)
     * @param item item used (also unused)
     */
    public void onUse(Entity e, ItemData item) {
        InventoryComponent bagInventory =
                (InventoryComponent) bagEntity.getComponent(InventoryComponent.class).get();
        InventoryComponent heroInventory =
                (InventoryComponent)
                        Game.getHero().get().getComponent(InventoryComponent.class).get();

        List<ItemData> temp;
        temp = bagInventory.getItems();
        bagInventory.setItems(heroInventory.getItems());
        heroInventory.setItems(temp);
    }

    public ItemType getInventoryType() {
        return inventoryType;
    }
}
