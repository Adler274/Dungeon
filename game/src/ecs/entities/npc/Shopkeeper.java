package ecs.entities.npc;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.InteractionComponent;
import ecs.components.InventoryComponent;
import ecs.components.PositionComponent;
import ecs.entities.Entity;
import ecs.entities.Hero;
import ecs.items.ItemData;
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
import tools.Point;

public class Shopkeeper extends Entity {

    private final float defaultInteractionRadius = 1f;
    private final int inventorySize = 3;
    private final String PathToIdleAnimation = "npc/shopkeeper/idle";
    private boolean riddleCleared = false;
    private InventoryComponent inventory;
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private int randInt;
    private String input;
    private Matcher matcher;
    private Pattern riddlePattern;
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

    /**
     * Shopkeeper asks a random riddle which has to be given a precise answer via scanner. If the
     * given answer matches the correct answer, the player gets access to the shop.
     */
    private void riddle() {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "Ah, welcome, traveler, to my humble establishment. But before I grant you access to the wonders within, you must prove your worth.\nBehold, a riddle I shall present. Solve it, and the doors of my shop shall open wide for you.");
            case 1 -> System.out.println(
                    " Ah, a potential customer stands before me. But before you can step foot into my realm, a challenge awaits.\nIf you can solve the riddle I present, you shall earn the privilege to access my shop.");
            case 2 -> System.out.println(
                    "Ah, a seeker of goods and knowledge approaches. But before you can enter my domain, a trial must be faced.\nSolve the riddle I present, and the gates shall open wide for you. ");
        }
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> {
                System.out.println("What does a baby computer call his father?");
                riddlePattern = Pattern.compile("Data", Pattern.CASE_INSENSITIVE);
            }
            case 1 -> {
                System.out.println("What did the buffalo say when his son left?");
                riddlePattern = Pattern.compile("Bison", Pattern.CASE_INSENSITIVE);
            }
            case 2 -> {
                System.out.println("What is red and bad for your teeth?");
                riddlePattern = Pattern.compile("A?\\s*Brick", Pattern.CASE_INSENSITIVE);
            }
        }
        shopLogger.log(
            CustomLogLevel.INFO, "Riddle was asked. (input: " + input + ")");
        matcher = riddlePattern.matcher(scanner.nextLine());
        if (matcher.matches()) {
            riddleCleared = true;
            shopLogger.log(
                CustomLogLevel.INFO, "Correct answer was given. (input: " + input + ")");
            randInt = random.nextInt(3);
            switch (randInt) {
                case 0 -> System.out.println(
                        "Well, well, it seems you've managed to solve my riddle. Color me impressed, or at least mildly surprised.\nI suppose you've earned the right to step foot in my esteemed establishment. Consider yourself privileged.");
                case 1 -> System.out.println(
                        "I must admit, I didn't think you had it in you. Well done, I suppose. You've proven yourself worthy of entry to my humble abode.\nStep inside and let's see if you have the same wit when it comes to striking a deal.\nBut remember, just because you solved one puzzle doesn't mean you can outsmart me in the realm of commerce.");
                case 2 -> System.out.println(
                        "You've earned yourself a chance to peruse my vast array of treasures.\nJust remember, while your wit may have granted you access, it won't grant you any special privileges when it comes to pricing.\nEnter and tread carefully, for your success in solving riddles does not guarantee success in bargaining.");
            }
        } else {
            shopLogger.log(
                CustomLogLevel.INFO, "Answer was not correct. (input: " + input + ")");
            randInt = random.nextInt(3);
            switch (randInt) {
                case 0 -> System.out.println(
                        "Hah! Looks like you couldn't solve my little riddle. I suppose I shouldn't be surprised.\nMost who enter here lack the wit to unravel its mysteries. You're just like the rest, a clueless fool.");
                case 1 -> System.out.println(
                        "Well, well, well. It seems you couldn't crack the riddle, just like the countless others who have tried and failed before you.\nDon't feel bad, though. Not everyone has the intellect to match wits with me.\nAccess to my shop is reserved for the clever and quick-minded, and you simply didn't make the cut. ");
                case 2 -> System.out.println(
                        "Oh, look at you, utterly stumped by a simple riddle.\nHow amusing. I had hoped for some semblance of intelligence, but alas, you've disappointed me. No entry to my shop for you, I'm afraid.\nOnly those who can prove their mental prowess are welcome here. Perhaps you should spend your time elsewhere, engaging in activities more suited to your limited capabilities.\nGood day to you.");
            }
        }
    }

    /**
     * Starts with random dialog and shows shopkeeper inventory with prices. Player can choose what
     * to do via scanner. If either buyPattern or sellPattern is found (only one), the corresponding
     * method process gets started.
     */
    private void selectBuyOrSell() {
        shopLogger.log(CustomLogLevel.INFO, "Shop has been opened.");
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "What do you want? Buying or selling? Make it quick, I don't have all day for indecisive fools like you.");
            case 1 -> System.out.println(
                    "Do you want to buy or sell something, or are you just here to waste my time?");
            case 2 -> System.out.println(
                    "Now, no more jokes. Let's get down to business. Buying or selling?");
        }

        // getting selection
        input = scanner.nextLine();
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
            shopLogger.log(CustomLogLevel.INFO, "No option was chosen. (input: " + input + ")");
            cancelConversation();
        }
    }

    /**
     * Starts with random dialog and shows shopkeeper inventory with prices. Player can enter an
     * item name via scanner. haggle() will get called if the item is found.
     */
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
        input = scanner.nextLine();
        for (ItemData item : inventory.getItems()) {
            matcher = item.getItemPattern().matcher(input);
            if (matcher.find()) {
                shopLogger.log(
                        CustomLogLevel.INFO,
                        "Matching item was found. (item: "
                                + item.getItemName()
                                + ", input: "
                                + input
                                + "), Starting haggling.");
                haggle(item);
                return;
            }
        }
        // only reached if no haggling happened
        shopLogger.log(CustomLogLevel.INFO, "No matching Item was found. (input: " + input + ")");
        cancelConversation();
    }

    /**
     * Starts with random dialog. Tries to get an offer via scanner. The offer has to be an Integer.
     * If the offer is no Integer, the process gets cancelled. If the offer is an Integer, the price
     * difference is compared to random Integer to check if haggling is successful. If yes, the hero
     * will try to pay for the item. If he can't, the price will get back to normal. If no. the
     * items price gets increased by one.
     *
     * @param item with price that gets haggled about
     */
    private void haggle(ItemData item) {
        randInt = random.nextInt(3);
        switch (randInt) {
            case 0 -> System.out.println(
                    "So, you want to play the game of haggling, do you? Fine, I'll entertain your offer.\nBut make it reasonable.Don't insult me with some absurdly low figure.\nI know the value of what I sell, and I won't part with my merchandise for a pittance.");
            case 1 -> System.out.println(
                    "Ah, feeling confident enough to make an offer, are you? Well, let's see if your proposal catches my attention.\nBut I warn you, I'm not one to be easily swayed. Make it a fair offer, or don't bother wasting my time.\nI've got other customers waiting, you know.");
            case 2 -> System.out.println(
                    "So, you think you can outwit me with your offer? I've been in this business long enough to spot a lowball when I see one.\nBut I'm in a generous mood today, so I'll humor you. Go ahead, name your price. Just don't get your hopes up");
        }
        Hero hero = (Hero) Game.getHero().get();
        int offer;
        if (scanner.hasNextInt()) {
            offer = scanner.nextInt();
        } else {
            input = scanner.nextLine();
            shopLogger.log(
                    CustomLogLevel.INFO,
                    "Invalid Input. Needs to be an Integer. (input: " + input + ")");
            cancelConversation();
            return;
        }
        int lowball = item.getPrice() - offer;
        randInt = random.nextInt(3);
        if (randInt >= lowball) { // haggling worked
            shopLogger.log(CustomLogLevel.INFO, "Haggling was successful. (offer: " + offer + ")");
            item.setPrice(offer);
            if (hero.takeMoney(item.getPrice())) { // enough money
                randInt = random.nextInt(3);
                switch (randInt) {
                    case 0 -> System.out.println(
                            "Well, looks like you've made a purchase. About time you put your money where your mouth is.\nJust make sure you take good care of that item. Consider it a final sale.\nNow, be on your way and let me get back to the business of making real profits.");
                    case 1 -> System.out.println(
                            "Ah, you've actually made a purchase. Surprising, but I suppose it's better than nothing.\nJust remember, once you walk out that door, it's no longer my concern. Don't come crying to me if you realize you've wasted your coin.\nNow, take your purchase and be gone. I've got no time for lingering customers like you.");
                    case 2 -> System.out.println(
                            "Just remember, there are no refunds or exchanges here. Once it's yours, it's yours.\nDon't come back expecting special treatment. Now, take your item and leave. I've got other customers to attend to.");
                }
                shopLogger.log(
                        CustomLogLevel.INFO,
                        "Hero was able to buy '"
                                + item.getItemName()
                                + "'. (remaining money: "
                                + hero.getMoney()
                                + ")");
                inventory.removeItem(item);
                item.triggerDrop(this, calculateDropPosition());
            } else { // to poor
                randInt = random.nextInt(3);
                switch (randInt) {
                    case 0 -> System.out.println(
                            " You've got to be kidding me. You come into my shop without enough coin? Do I look like a charity to you?\nI run a business here, not a handout service. If you can't afford it, then don't waste my time.\nCome back when you have enough money to actually make a purchase. Until then, get out of my sight.");
                    case 1 -> System.out.println(
                            "Are you trying to pull a fast one on me? Coming in here with empty pockets? I'm not in the business of charity, my friend.\nIf you can't pay for what you want, then you can't have it. Simple as that.\nDon't waste any more of my time with your penniless attempts. Come back when you've got the coin to back up your desires.");
                    case 2 -> System.out.println(
                            "Seriously? You think you can walk in here, all empty-handed, and expect to buy something? This is a place of business, not a charity for the destitute.\nIf you can't afford what you're after, then move along. I have no time for time-wasters and broke wanderers.\nCome back when you've got the means to actually make a purchase. Now, leave and let the real customers through.");
                }
                shopLogger.log(
                        CustomLogLevel.INFO,
                        "Hero was unable to buy '"
                                + item.getItemName()
                                + "'. (money: "
                                + hero.getMoney()
                                + ")");
                item.setPrice(item.getPrice() + lowball);
            }
        } else { // haggling did not work
            shopLogger.log(
                    CustomLogLevel.INFO, "Haggling was unsuccessful. (offer: " + offer + ")");
            randInt = random.nextInt(3);
            switch (randInt) {
                case 0 -> System.out.println(
                        "Oh, you think you can haggle with me and offer a pitiful sum? Well, guess what? I've changed my mind. The price just went up, out of pure spite.\nIf you're going to waste my time with your laughable offers, then you'll pay the price for it. Consider it a lesson in respect.\nNow, if you're willing to cough up the proper amount, we can continue our negotiation. But don't think for a second that I'll go easy on you.");
                case 1 -> System.out.println(
                        "Ha! Is this some kind of joke? Your offer is an insult, plain and simple. I won't be swayed by your feeble attempts at negotiation.\nIn fact, I'm increasing the price just to teach you a lesson.If you want my goods, you'll pay the price I demand or leave empty-handed.\nI have no time for cheapskates like you.");
                case 2 -> System.out.println(
                        "Oh, how amusing! You think you can lowball me and expect to strike a deal? Well, I've got news for you. I'm not in the mood for your games.\nI'm raising the price, just to show you what happens when you try to play hardball with me.\nIf you're not willing to meet my fair asking price, then you can find another shop to waste your time in.");
            }
            item.setPrice(item.getPrice() + 1);
        }
    }

    /**
     * Starts with random dialog and shows player inventory with selling prices. Player can enter an
     * item name via scanner. The item will get sold if found in the player's inventory.
     */
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
        InventoryComponent heroInventory =
                (InventoryComponent)
                        Game.getHero().get().getComponent(InventoryComponent.class).get();
        heroInventory.printInventory(0.7f);
        input = scanner.nextLine();
        for (ItemData item : heroInventory.getItems()) {
            matcher = item.getItemPattern().matcher(input);
            if (matcher.find()) {
                ((Hero) Game.getHero().get()).addMoney((int) (item.getPrice() * 0.7f));
                shopLogger.log(
                        CustomLogLevel.INFO,
                        "Hero sold '"
                                + item.getItemName()
                                + "'. (money: "
                                + ((Hero) Game.getHero().get()).getMoney()
                                + ")");
                heroInventory.removeItem(item);
                return;
            }
        }
        // only reached if no selling happened
        shopLogger.log(CustomLogLevel.INFO, "No matching Item was found. (input: " + input + ")");
        cancelConversation();
    }

    /** Random dialog to be called when the conversation ends without making a transaction */
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

    /**
     * small Helper to determine the Position of the dropped item
     *
     * @return the Point one tile below the Shopkeeper
     */
    private Point calculateDropPosition() {
        PositionComponent pc = (PositionComponent) this.getComponent(PositionComponent.class).get();
        return new Point(pc.getPosition().x, (pc.getPosition().y) - 1);
    }
}
