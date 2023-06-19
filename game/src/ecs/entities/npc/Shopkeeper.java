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
        input = scanner.nextLine();
        for (ItemData item : inventory.getItems()) {
            matcher = item.getItemPattern().matcher(input);
            if (matcher.find()) {
                haggle(item);
                return;
            }
        }
        // only reached if no haggling happened
        cancelConversation();
    }

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
        int offer = scanner.nextInt();
        int lowball = item.getPrice() - offer;
        randInt = random.nextInt(3);
        if (randInt >= lowball) { // haggling worked
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
                item.setPrice(item.getPrice() + lowball);
            }
        } else { // haggling did not work
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
                heroInventory.removeItem(item);
                return;
            }
        }
        // only reached if no selling happened
        cancelConversation();
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
