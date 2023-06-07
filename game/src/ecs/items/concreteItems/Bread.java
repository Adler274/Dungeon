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
                "restores 5 HP to the user");
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
