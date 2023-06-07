package ecs.components;

import ecs.entities.Entity;
import ecs.items.ItemData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import logging.CustomLogLevel;

/** Allows an Entity to carry Items */
public class InventoryComponent extends Component {

    private List<ItemData> inventory;
    private int maxSize;
    private final Logger inventoryLogger = Logger.getLogger(this.getClass().getName());

    /**
     * creates a new InventoryComponent
     *
     * @param entity the Entity where this Component should be added to
     * @param maxSize the maximal size of the inventory
     */
    public InventoryComponent(Entity entity, int maxSize) {
        super(entity);
        inventory = new ArrayList<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Adding an Element to the Inventory does not allow adding more items than the size of the
     * Inventory.
     *
     * @param itemData the item which should be added
     * @return true if the item was added, otherwise false
     */
    public boolean addItem(ItemData itemData) {
        if (inventory.size() >= maxSize) return false;
        inventoryLogger.log(
                CustomLogLevel.DEBUG,
                "Item '"
                        + itemData.getItemName()
                        + "' was added to the inventory of entity '"
                        + entity.getClass().getSimpleName()
                        + "'.");
        return inventory.add(itemData);
    }

    /**
     * removes the given Item from the inventory
     *
     * @param itemData the item which should be removed
     * @return true if the element was removed, otherwise false
     */
    public boolean removeItem(ItemData itemData) {
        inventoryLogger.log(
                CustomLogLevel.DEBUG,
                "Removing item '"
                        + itemData.getItemName()
                        + "' from inventory of entity '"
                        + entity.getClass().getSimpleName()
                        + "'.");
        return inventory.remove(itemData);
    }

    /**
     * @return the number of slots already filled with items
     */
    public int filledSlots() {
        return inventory.size();
    }

    /**
     * @return the number of slots still empty
     */
    public int emptySlots() {
        return maxSize - inventory.size();
    }

    /**
     * @return the size of the inventory
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return a copy of the inventory
     */
    public List<ItemData> getItems() {
        return new ArrayList<>(inventory);
    }

    /** shows the heros inventory using a logger */
    public void showInventory() {
        StringBuilder inv = new StringBuilder("Inventory opened");
        if (inventory.size() != 0) {
            for (int i = 0; i < inventory.size(); i++) {
                inv.append("\n").append(i + 1).append(": ").append(inventory.get(i).getItemName());
            }
        }
        inventoryLogger.log(CustomLogLevel.INFO, inv.toString());
    }

    /**
     * @param index index of the item to be used
     * @param user entity who uses the item
     */
    public void useItem(int index, Entity user) {
        if (inventory.size() > index) {
            inventoryLogger.log(
                    CustomLogLevel.INFO, inventory.get(index).getItemName() + " was used \nInventory closed");
            inventory.get(index).triggerUse(user);
        }
    }
}
