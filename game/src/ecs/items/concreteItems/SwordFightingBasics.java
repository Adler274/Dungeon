package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.components.InventoryComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.*;
import starter.Game;
import tools.Point;

public class SwordFightingBasics extends ItemData {

    public SwordFightingBasics() {
        super(
                ItemType.BOOK,
                AnimationBuilder.buildAnimation("missingTexture.png"),
                AnimationBuilder.buildAnimation("missingTexture.png"),
                "SwordFightingBasics",
                "Adds a SwordSkill skill to the hero's SkillComponent");
        this.setOnCollect(this::onCollect);
        this.setOnDrop(this::onDrop);
        this.setOnUse(this::onUse);
    }

    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {}

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
