package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.HealthComponent;
import ecs.components.InventoryComponent;
import ecs.components.MissingComponentException;
import ecs.entities.Entity;
import ecs.items.*;
import starter.Game;
import tools.Point;

public class Bread extends ItemData {

    public Bread() {
        super(
                ItemType.FOOD,
                AnimationBuilder.buildAnimation("ui_heart_full.png"),   // TODO change
                AnimationBuilder.buildAnimation("ui_heart_full.png"),   // TODO change
                "Bread",
                "Heals the hero by 5 health points");
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

    /**
     * @param e entity that uses the item
     * @param item the items being used
     */
    public void onUse(Entity e, ItemData item) {
        // healing
        HealthComponent hc =
                (HealthComponent)
                        e.getComponent(HealthComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("HealthComponent"));
        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() + 5);

        // removing item
        e.getComponent(InventoryComponent.class)
                .ifPresent(ic -> ((InventoryComponent) ic).removeItem(this));
    }
}
