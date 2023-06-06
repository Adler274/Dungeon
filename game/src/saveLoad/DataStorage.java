package saveLoad;

import java.io.Serializable;
import java.util.ArrayList;

/** DataStorage objects are used to store the savedata in a savefile */
public class DataStorage implements Serializable {
    /** Stores the name of entities as Strings */
    private ArrayList<String> entityList;

    private int levelCount;
    private int playerHealth;
    private long playerLevel;
    private long playerXP;
    private boolean hasGhost;
    private int heroClass;

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

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public long getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(long playerLevel) {
        this.playerLevel = playerLevel;
    }

    public long getPlayerXP() {
        return playerXP;
    }

    public void setPlayerXP(long playerXP) {
        this.playerXP = playerXP;
    }

    public int getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(int heroClass) {
        this.heroClass = heroClass;
    }
}
