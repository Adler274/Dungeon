package ecs.entities.npc;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.InteractionComponent;
import ecs.components.InventoryComponent;
import ecs.components.PositionComponent;
import ecs.entities.Entity;
import ecs.systems.PlayerSystem;
import graphic.Animation;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import logging.CustomLogLevel;
import starter.Game;

public class Shopkeeper extends Entity {

    private final float defaultInteractionRadius = 1f;
    private final int inventorySize = 3;
    private final String PathToIdleAnimation = "missingTexture.png";
    private boolean riddleCleared = false;
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
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
        InventoryComponent ic = new InventoryComponent(this, inventorySize);
        // add random Items
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
        // riddle
        if (true) {
            riddleCleared = true;
        }
    }

    private void selectBuyOrSell() {
        // selection
        if (true) {
            shopLogger.log(CustomLogLevel.INFO, "Option 'Buy' was chosen.");
            buy();
        } else if (false) {
            shopLogger.log(CustomLogLevel.INFO, "Option 'Sell' was chosen.");
            sell();
        } else {
            shopLogger.log(CustomLogLevel.INFO, "No option was chosen. Ending process.");
        }
    }

    private void buy() {
        // buying process
    }

    private void sell() {}
    // selling process
}
