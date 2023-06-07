package ecs.items.explicitItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.*;
import starter.Game;
import tools.Point;

import java.util.ArrayList;
import java.util.List;

public class Bag extends ItemData implements IOnCollect, IOnDrop, IOnUse {

    private List <ItemData> inventory;
    private final int max = 3;
    public Bag(){
        super(
            ItemType.BAG,
            AnimationBuilder.buildAnimation("missingTexture.png"),
            AnimationBuilder.buildAnimation("missingTexture.png"),
            "Bag",
            "Holds items for the hero");
        this.setOnCollect(this);
        this.setOnDrop(this);
        this.setOnUse(this);


        WorldItemBuilder.buildWorldItem(this);
        inventory = new ArrayList<>(3);
    }


    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {
        if (whoCollides instanceof Hero hero){
            Game.removeEntity(WorldItemEntity);
            InventoryComponent inventar = hero.getInventar();
        }

    }

    public boolean addItem(ItemData itemData){
        if (inventory.size() >= max){
            return false;

        }
        return inventory.add(itemData);

    }

    @Override
    public void onDrop(Entity user, ItemData which, Point position) {

    }

    @Override
    public void onUse(Entity e, ItemData item) {

    }
}
