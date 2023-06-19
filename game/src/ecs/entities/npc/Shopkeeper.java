package ecs.entities.npc;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.InteractionComponent;
import ecs.components.InventoryComponent;
import ecs.components.PositionComponent;
import ecs.entities.Entity;
import ecs.items.ItemDataGenerator;
import ecs.systems.PlayerSystem;
import graphic.Animation;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import logging.CustomLogLevel;
import starter.Game;

public class Shopkeeper extends Entity {

    private final float defaultInteractionRadius = 1f;
    private final int inventorySize = 3;
    private final String PathToIdleAnimation = "npc/shopkeeper/idle";
    private boolean riddleCleared = false;
    private InventoryComponent inventory;
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private int randInt;
    private Matcher matcher;
    private static final Pattern riddlePattern =
            Pattern.compile("answer", Pattern.CASE_INSENSITIVE); // TODO change answer
    private static final Pattern buyPattern = Pattern.compile("buy", Pattern.CASE_INSENSITIVE);
    private static final Pattern sellPattern = Pattern.compile("sell", Pattern.CASE_INSENSITIVE);
    private final Logger shopLogger = Logger.getLogger(this.getClass().getSimpleName());

    /** Entity with Components. Creates a Shopkeeper to buy/sell items after clearing a riddle. */
    public Shopkeeper() {
        new PositionComponent(this);
        new InteractionComponent(this, defaultInteractionRadius, true, this::onInteract);
        setupAnimationComponent();
        setupInventoryComponent();
    }

    /**
     * Helper method to set up an AnimationComponent using the PathToIdleAnimation attribute to set
     * a single animation
     */
    private void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(PathToIdleAnimation);
        new AnimationComponent(this, idle);
    }

    /** Helper method to create an InventoryComponent and fill it with random Items */
    private void setupInventoryComponent() {
        inventory = new InventoryComponent(this, inventorySize);
        ItemDataGenerator itemGenerator = new ItemDataGenerator();
        for (int i = 0; i < inventorySize; i++) {
            inventory.addItem(itemGenerator.generateItemData());
        }
    }

    /**
     * Pauses PlayerSystem, calls another method (based on riddleCleared), unpauses PlayerSystem
     * upon end of interaction
     *
     * @param entity interacting entity (does not do anything)
     */
    private void onInteract(Entity entity) {
        Game.systems.toggleSystem(PlayerSystem.class);
        if (!riddleCleared) {
            riddle();
        } else {
            selectBuyOrSell();
        }
        Game.systems.toggleSystem(PlayerSystem.class);
    }

    private void riddle() {
        // TODO add logging
        System.out.println("This is a riddle"); // TODO riddle
        matcher = riddlePattern.matcher(scanner.nextLine());
        if (matcher.matches()) {
            riddleCleared = true;
            System.out.println("riddle cleared"); // TODO change dialog
        } else {
            System.out.println("idiot"); // TODO change dialog
        }
    }

    private void selectBuyOrSell() {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "What do you want? Buying or selling? Make it quick, I don't have all day for indecisive fools like you.");
            case 1 -> System.out.println(
                    "Do you want to buy or sell something, or are you just here to waste my time?");
            case 2 -> System.out.println(
                    "Now, no more jokes. Let's get down to business. Buying or selling?");
        }
        shopLogger.log(CustomLogLevel.INFO, "Shop has been opened.");

        // getting selection
        String input = scanner.nextLine();
        matcher = buyPattern.matcher(input);
        boolean buy = matcher.find();
        matcher = sellPattern.matcher(input);
        boolean sell = matcher.find();

        // processing selection
        if (buy && !sell) {
            shopLogger.log(
                    CustomLogLevel.INFO, "Option 'Buying' was chosen. (input: " + input + ")");
            buy();
        } else if (sell && !buy) {
            shopLogger.log(
                    CustomLogLevel.INFO, "Option 'Selling' was chosen. (input: " + input + ")");
            sell();
        } else {
            shopLogger.log(
                    CustomLogLevel.INFO,
                    "No option was chosen. Ending process. (input: " + input + ")");
            cancelConversation();
        }
    }

    private void buy() {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "Ah, so you're finally interested in spending your coin. About time. Show me, what you're looking for.");
            case 1 -> System.out.println(
                    "So, you're actually considering buying something? Well, I suppose I can spare a moment for a potential customer. Let's see what catches your fancy.");
            case 2 -> System.out.println(
                    "Ah, a customer with the intention to spend. You've come to the right place. Take a good look at my wares, but don't waste my time with idle browsing.");
        }
        inventory.printInventory(1f);
        // TODO actual buying
    }

    private void sell() {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "Ah, so you think you have something worth selling?\nLet's see what you've got then.Don't get your hopes up though, I won't be easily impressed.\nI'll give you a fair price if it's worth my while, but don't expect any miracles. ");
            case 1 -> System.out.println(
                    "Ah, another hopeful soul looking to offload their wares. Show me what you've got, but I warn you, I'm not in the business of charity.\nI'll evaluate your goods and offer a price that reflects their value. If you're looking for a quick buck, you're better off elsewhere.\nLet's get on with it then, show me what you want to sell.");
            case 2 -> System.out.println(
                    "Ah, another would-be peddler, hoping to make a quick coin. Well, let's see what you've brought to the table.\nKeep in mind, I've seen it all, so don't think your trinkets hold any special value to me.");
        }
        Game.getHero()
                .get()
                .getComponent(InventoryComponent.class)
                .ifPresent(ic -> ((InventoryComponent) ic).printInventory(0.7f));
        // TODO actual selling
    }

    private void cancelConversation() {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "Oh, walking away empty-handed, are we? Typical. Just another tire kicker wasting my time.\nDon't bother coming back unless you plan on actually buying something. ");
            case 1 -> System.out.println(
                    "Well, it seems you've decided to leave without making a transaction. Fine by me.\nI've got plenty of other customers who know what they want. No need to stick around if you're not serious about buying or selling.\nOff you go now, and let me get back to serving customers who actually contribute to my livelihood.");
            case 2 -> System.out.println(
                    "Leaving already? I'm not here to entertain window shoppers.\nIf you change your mind and decide to come back with some real business, maybe I'll consider giving you the time of day.\nBut until then, don't waste any more of my precious time.");
        }
    }
}
