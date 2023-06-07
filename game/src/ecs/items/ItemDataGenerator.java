package ecs.items;

import ecs.items.concreteItems.Bag;
import ecs.items.concreteItems.Bread;
import ecs.items.concreteItems.ExperiencePotion;
import ecs.items.concreteItems.SwordFightingBasics;
import java.util.List;
import java.util.Random;

/** Generator which creates a random ItemData based on the Templates prepared. */
public class ItemDataGenerator {
    private static final List<String> missingTexture = List.of("animation/missingTexture.png");

    private List<ItemData> templates =
            List.of(new Bread(), new ExperiencePotion(), new SwordFightingBasics(), new Bag());
    private Random rand = new Random();

    /**
     * @return a new randomItemData
     */
    public ItemData generateItemData() {
        return templates.get(rand.nextInt(templates.size()));
    }
}
