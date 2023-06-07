package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.components.ItemComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.*;
import starter.Game;
import tools.Point;

public class SwordFightingBasics extends ItemData {

    public SwordFightingBasics() {
        super(
                ItemType.BOOK,
                AnimationBuilder.buildAnimation("ui_heart_half.png"),   // TODO change
                AnimationBuilder.buildAnimation("ui_heart_half.png"),   // TODO change
                "SwordFightingBasics",
                "Adds a SwordSkill skill to the hero's SkillComponent");
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
        // learning skill if used by hero
        if (e == Game.getHero().get()) {
            Hero hero = (Hero) Game.getHero().get();
            hero.learnSwordSkill();
        }

        // removing item
        e.getComponent(InventoryComponent.class)
                .ifPresent(ic -> ((InventoryComponent) ic).removeItem(this));
    }
}
