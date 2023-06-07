package ecs.items.concreteItems;

import dslToGame.AnimationBuilder;
import ecs.entities.Entity;
import ecs.items.*;
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

    public void onUse(Entity e, ItemData item) {}
}
