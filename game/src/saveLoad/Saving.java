package saveLoad;

import ecs.entities.Entity;
import ecs.entities.Tombstone;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import ecs.entities.npc.Ghost;
import starter.Game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Saving is used to save and load savedata using a DataStorage object and files
 */
public class Saving {

    /** Game Instance which data is to be managed*/
    private final Game game;
    public Saving(Game game){
        this.game = game;
    }

    /** Writes Savedata into a savefile*/
    public void writeSave(){
        DataStorage data = new DataStorage();

        data.setLevelCount(game.getLevelCount());
        data.setHasGhost(game.isHasGhost());

        ArrayList<String> entityList = new ArrayList<>();
        for(Entity entity : Game.getEntities()){
            entityList.add(entity.getClass().getSimpleName());
        }
        data.setEntityList(entityList);

        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = new FileOutputStream("savefile\\Save.ser");
            out = new ObjectOutputStream(fos);
            out.writeObject(data);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Loads savedata from a savefile*/
    public void loadSave(){
        FileInputStream fis;
        ObjectInputStream in;
        DataStorage data = new DataStorage();
        try {
            fis = new FileInputStream("savefile\\Save.ser");
            in = new ObjectInputStream(fis);
            data = (DataStorage) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        game.setLevelCount(data.getLevelCount());
        game.setHasGhost(data.isHasGhost());
        for(String entity : data.getEntityList()){
            switch (entity) {
                case "OrcNormal" -> Game.getEntities().add(new OrcNormal());
                case "OrcBaby" -> Game.getEntities().add(new OrcBaby());
                case "OrcMasked" -> Game.getEntities().add(new OrcMasked());
                case "Ghost" -> {
                    Ghost ghost = new Ghost(); Game.getEntities().add(ghost);
                    Tombstone tombstone = new Tombstone(ghost); Game.getEntities().add(tombstone);
                    game.setTomb(tombstone);
                }
            }
        }
    }
}
