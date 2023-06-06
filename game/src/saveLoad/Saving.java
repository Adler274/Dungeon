package saveLoad;

import ecs.components.HealthComponent;
import ecs.components.xp.XPComponent;
import ecs.entities.*;
import ecs.entities.Character;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import ecs.entities.npc.Ghost;
import ecs.entities.traps.SlowTrap;
import ecs.entities.traps.SpawnerTrap;
import ecs.entities.traps.TrapSwitch;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import starter.Game;

/** Saving is used to save and load savedata using a DataStorage object and files */
public class Saving {

    /** Game Instance which data is to be managed */
    private final Game game;

    public Saving(Game game) {
        this.game = game;
    }

    /** Writes Savedata into a savefile */
    public void writeSave() {
        DataStorage data = new DataStorage();

        data.setLevelCount(game.getLevelCount());
        data.setHasGhost(game.isHasGhost());
        Game.getHero()
                .get()
                .getComponent(HealthComponent.class)
                .ifPresent(
                        hc -> {
                            data.setPlayerHealth(((HealthComponent) hc).getCurrentHealthpoints());
                        });
        Game.getHero()
                .get()
                .getComponent(XPComponent.class)
                .ifPresent(
                        xc -> {
                            data.setPlayerLevel(((XPComponent) xc).getCurrentLevel());
                            data.setPlayerXP(((XPComponent) xc).getCurrentXP());
                        });
        data.setHeroClass(((Hero) Game.getHero().get()).getHeroClass());

        ArrayList<String> entityList = new ArrayList<>();
        for (Entity entity : Game.getEntities()) {
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

    /** Loads savedata from a savefile */
    public void loadSave() {
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
        int playerHealth = data.getPlayerHealth();
        long playerLevel = data.getPlayerLevel();
        long playerXP = data.getPlayerXP();

        switch (data.getHeroClass()) {
            case 1 -> Game.setHero(new Hero(Character.WIZARD));
            case 2 -> Game.setHero(new Hero(Character.KNIGHT));
            case 3 -> Game.setHero(new Hero(Character.ELF));
            default -> Game.setHero(new Hero(Character.DEBUG));
        }
        Game.getHero()
                .get()
                .getComponent(HealthComponent.class)
                .ifPresent(
                        hc -> {
                            ((HealthComponent) hc).setCurrentHealthpoints(playerHealth);
                        });
        Game.getHero()
                .get()
                .getComponent(XPComponent.class)
                .ifPresent(
                        xc -> {
                            for (long current = 1; current <= playerLevel; current++) {
                                ((XPComponent) xc).levelUp(current);
                            }
                            ((XPComponent) xc).setCurrentXP(playerXP);
                        });
        for (String entity : data.getEntityList()) {
            switch (entity) {
                case "OrcNormal" -> Game.getEntities().add(new OrcNormal());
                case "OrcBaby" -> Game.getEntities().add(new OrcBaby());
                case "OrcMasked" -> Game.getEntities().add(new OrcMasked());
                case "Ghost" -> {
                    Ghost ghost = new Ghost();
                    Game.getEntities().add(ghost);
                    Tombstone tombstone = new Tombstone(ghost);
                    Game.getEntities().add(tombstone);
                    game.setTomb(tombstone);
                }
                case "SlowTrap" -> Game.getEntities().add(new SlowTrap());
                case "SpawnerTrap" -> {
                    SpawnerTrap spawnerT = new SpawnerTrap();
                    Game.getEntities().add(spawnerT);
                    Game.getEntities().add(new TrapSwitch(spawnerT));
                }
            }
        }
    }
}
