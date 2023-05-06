package saveLoad;

import java.io.Serializable;
import java.util.ArrayList;

/** DataStorage objects are used to store the savedata in a savefile*/
public class DataStorage implements Serializable {
    /** Stores the name of entities as Strings */
    private ArrayList<String> entityList;
    private int levelCount;
    private boolean hasGhost;

    public ArrayList<String> getEntityList() {
        return entityList;
    }

    public void setEntityList(ArrayList<String> entityList) {
        this.entityList = entityList;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }

    public boolean isHasGhost() {
        return hasGhost;
    }

    public void setHasGhost(boolean hasGhost) {
        this.hasGhost = hasGhost;
    }
}
