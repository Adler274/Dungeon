package ecs.items;

import ecs.entities.Entity;
import graphic.Animation;

public abstract class Item extends Entity {
    protected ItemType itemType;
    protected Animation inventoyTexture;
    protected Animation worldTexture;
    protected String itemName;
    protected String description;

    public Item(ItemType itemType,Animation inventoyTexture,Animation worldTexture,String itemName,String description){
        super();
        this.itemType=itemType;
        this.inventoyTexture=inventoyTexture;
        this.worldTexture=worldTexture;
        this.itemName=itemName;
        this.description=description;
    }

    protected void setupItemComponent(){};

    protected void onCollect(Entity worldItemEntity,Entity whoCollides){};

}
