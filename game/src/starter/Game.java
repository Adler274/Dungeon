package starter;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static logging.LoggerConfig.initBaseLogger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import configuration.Configuration;
import configuration.KeyboardConfig;
import controller.AbstractController;
import controller.SystemController;
import ecs.components.*;
import ecs.components.skill.DamageMeleeSkill;
import ecs.entities.*;
import ecs.entities.monster.OrcBaby;
import ecs.entities.monster.OrcMasked;
import ecs.entities.monster.OrcNormal;
import ecs.entities.npc.Ghost;
import ecs.entities.traps.SlowTrap;
import ecs.entities.traps.SpawnerTrap;
import ecs.entities.traps.TrapSwitch;
import ecs.systems.*;
import graphic.DungeonCamera;
import graphic.Painter;
import graphic.hud.GameOverMenu;
import graphic.hud.HeroSelection;
import graphic.hud.PauseMenu;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import level.IOnLevelLoader;
import level.LevelAPI;
import level.elements.ILevel;
import level.elements.tile.Tile;
import level.generator.IGenerator;
import level.generator.postGeneration.WallGenerator;
import level.generator.randomwalk.RandomWalkGenerator;
import level.tools.LevelSize;
import logging.CustomLogLevel;
import saveLoad.Saving;
import tools.Constants;
import tools.Point;

/** The heart of the framework. From here all strings are pulled. */
public class Game extends ScreenAdapter implements IOnLevelLoader {

    private final LevelSize LEVELSIZE = LevelSize.SMALL;

    /**
     * The batch is necessary to draw ALL the stuff. Every object that uses draw need to know the
     * batch.
     */
    protected SpriteBatch batch;

    /** Contains all Controller of the Dungeon */
    protected List<AbstractController<?>> controller;

    public static DungeonCamera camera;
    /** Draws objects */
    protected Painter painter;

    protected LevelAPI levelAPI;
    /** Generates the level */
    protected IGenerator generator;

    private static Game game;
    private boolean doSetup = true;
    private static boolean paused = false;

    /** All entities that are currently active in the dungeon */
    private static final Set<Entity> entities = new HashSet<>();
    /** All entities to be removed from the dungeon in the next frame */
    private static final Set<Entity> entitiesToRemove = new HashSet<>();
    /** All entities to be added from the dungeon in the next frame */
    private static final Set<Entity> entitiesToAdd = new HashSet<>();

    /** List of all Systems in the ECS */
    public static SystemController systems;

    public static ILevel currentLevel;
    private static GameOverMenu<Actor> gameOverMenu;
    private static PauseMenu<Actor> pauseMenu;
    private static HeroSelection<Actor> heroSelection;
    private static Entity hero;
    private Logger gameLogger;

    /** Number of current level */
    private static int levelCount;
    /** Used to save and load savedata using files */
    private final Saving saving = new Saving(this);
    /** Used to check if you have to check if a ghost is near a tombstone */
    private boolean hasGhost;
    /** Needed so the object can be used to trigger its effect (in frame()) */
    private Tombstone tomb;

    public static void main(String[] args) {
        // start the game
        try {
            Configuration.loadAndGetConfiguration("dungeon_config.ser", KeyboardConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DesktopLauncher.run(game = new Game());
    }

    /**
     * Main game loop. Redraws the dungeon and calls the own implementation (beginFrame, endFrame
     * and onLevelLoad).
     *
     * @param delta Time since last loop.
     */
    @Override
    public void render(float delta) {
        if (doSetup) setup();
        batch.setProjectionMatrix(camera.combined);
        frame();
        clearScreen();
        levelAPI.update();
        controller.forEach(AbstractController::update);
        camera.update();
    }

    /** Called once at the beginning of the game. */
    protected void setup() {
        doSetup = false;
        controller = new ArrayList<>();
        setupCameras();
        painter = new Painter(batch, camera);
        generator = new RandomWalkGenerator();
        levelAPI = new LevelAPI(batch, painter, generator, this);
        initBaseLogger();
        gameLogger = Logger.getLogger(this.getClass().getName());
        systems = new SystemController();
        controller.add(systems);
        pauseMenu = new PauseMenu<>();
        controller.add(pauseMenu);
        gameOverMenu = new GameOverMenu<>();
        controller.add(gameOverMenu);
        levelCount = 0;

        levelAPI = new LevelAPI(batch, painter, new WallGenerator(new RandomWalkGenerator()), this);
        levelAPI.loadLevel(LEVELSIZE);
        createSystems();
    }

    /** Called at the beginning of each frame. Before the controllers call <code>update</code>. */
    protected void frame() {
        setCameraFocus();
        DamageMeleeSkill.update();
        if (hasGhost) {
            tomb.despawnAllMonsters();
        }
        manageEntitiesSets();
        getHero().ifPresent(this::loadNextLevelIfEntityIsOnEndTile);
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) togglePause();
    }

    @Override
    public void onLevelLoad() {
        currentLevel = levelAPI.getCurrentLevel();
        entities.clear();
        if (levelCount == 0 && new File("savefile\\Save.ser").exists()) {
            saving.loadSave();
            return;
        } else if (levelCount == 0) { // fresh save
            heroSelection = new HeroSelection<>();
            controller.add(heroSelection);
            heroSelection.showMenu();
        }
        getHero().ifPresent(this::placeOnLevelStart);
        levelCount++;
        spawnMonsters();
        spawnGhost();
        spawnTraps();
        if (levelCount > 1) {
            saving.writeSave();
        }
    }

    /** Restarts the game on level 1 */
    public static void restart() {
        gameOverMenu.hideMenu();
        removeEntity(hero);
        levelCount = 0;
        game.setup();
    }

    public static void chooseClass() {
        hero = new Hero(heroSelection.getHeroClass());
        heroSelection.hideMenu();
    }

    /** Closes the game safely */
    public static void end() {
        Gdx.app.exit();
        System.exit(0);
    }

    private void manageEntitiesSets() {
        entities.removeAll(entitiesToRemove);
        entities.addAll(entitiesToAdd);
        for (Entity entity : entitiesToRemove) {
            gameLogger.log(
                    CustomLogLevel.DEBUG,
                    "Entity '" + entity.getClass().getSimpleName() + "' was deleted.");
        }
        for (Entity entity : entitiesToAdd) {
            gameLogger.log(
                    CustomLogLevel.DEBUG,
                    "Entity '" + entity.getClass().getSimpleName() + "' was added.");
        }
        entitiesToRemove.clear();
        entitiesToAdd.clear();
    }

    private void setCameraFocus() {
        if (getHero().isPresent()) {
            PositionComponent pc =
                    (PositionComponent)
                            getHero()
                                    .get()
                                    .getComponent(PositionComponent.class)
                                    .orElseThrow(
                                            () ->
                                                    new MissingComponentException(
                                                            "PositionComponent"));
            camera.setFocusPoint(pc.getPosition());

        } else camera.setFocusPoint(new Point(0, 0));
    }

    private void loadNextLevelIfEntityIsOnEndTile(Entity hero) {
        if (isOnEndTile(hero)) levelAPI.loadLevel(LEVELSIZE);
    }

    private boolean isOnEndTile(Entity entity) {
        PositionComponent pc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        Tile currentTile = currentLevel.getTileAt(pc.getPosition().toCoordinate());
        return currentTile.equals(currentLevel.getEndTile());
    }

    private void placeOnLevelStart(Entity hero) {
        entities.add(hero);
        PositionComponent pc =
                (PositionComponent)
                        hero.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        pc.setPosition(currentLevel.getStartTile().getCoordinate().toPoint());
        // reset speed
        VelocityComponent vc =
                (VelocityComponent)
                        hero.getComponent(VelocityComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("VelocityComponent"));
        vc.setXVelocity(((Hero) hero).getXSpeed());
        vc.setYVelocity(((Hero) hero).getYSpeed());
        // heal 1 health
        HealthComponent hc =
                (HealthComponent)
                        hero.getComponent(HealthComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("HealthComponent"));
        hc.setCurrentHealthpoints(hc.getCurrentHealthpoints() + 1);
    }

    /** Toggle between pause and run */
    public static void togglePause() {
        paused = !paused;
        if (systems != null) {
            systems.forEach(ECS_System::toggleRun);
        }
        if (pauseMenu != null) {
            if (paused) pauseMenu.showMenu();
            else pauseMenu.hideMenu();
        }
    }

    /**
     * Given entity will be added to the game in the next frame
     *
     * @param entity will be added to the game next frame
     */
    public static void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    /**
     * Given entity will be removed from the game in the next frame
     *
     * @param entity will be removed from the game next frame
     */
    public static void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    /**
     * @return Set with all entities currently in game
     */
    public static Set<Entity> getEntities() {
        return entities;
    }

    /**
     * @return Set with all entities that will be added to the game next frame
     */
    public static Set<Entity> getEntitiesToAdd() {
        return entitiesToAdd;
    }

    /**
     * @return Set with all entities that will be removed from the game next frame
     */
    public static Set<Entity> getEntitiesToRemove() {
        return entitiesToRemove;
    }

    /**
     * @return the player character, can be null if not initialized
     */
    public static Optional<Entity> getHero() {
        return Optional.ofNullable(hero);
    }

    /**
     * set the reference of the playable character careful: old hero will not be removed from the
     * game
     *
     * @param hero new reference of hero
     */
    public static void setHero(Entity hero) {
        Game.hero = hero;
    }

    public void setSpriteBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void setupCameras() {
        camera = new DungeonCamera(null, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.zoom = Constants.DEFAULT_ZOOM_FACTOR;

        // See also:
        // https://stackoverflow.com/questions/52011592/libgdx-set-ortho-camera
    }

    private void createSystems() {
        new VelocitySystem();
        new DrawSystem(painter);
        new PlayerSystem();
        new AISystem();
        new CollisionSystem();
        new HealthSystem();
        new XPSystem();
        new SkillSystem();
        new ProjectileSystem();
    }

    /** Used to spawn monsters randomly based on the current level */
    private void spawnMonsters() {
        if (levelCount < 4) {
            int normalCount = ThreadLocalRandom.current().nextInt(1, 4);
            for (int i = 0; i < normalCount; i++) {
                entities.add(new OrcNormal());
            }
        } else if (levelCount < 7) {
            int normalCount = ThreadLocalRandom.current().nextInt(2, 4);
            int babyCount = ThreadLocalRandom.current().nextInt(0, 3);
            for (int i = 0; i < normalCount; i++) {
                entities.add(new OrcNormal());
            }
            for (int i = 0; i < babyCount; i++) {
                entities.add(new OrcBaby());
            }
        } else if (levelCount < 10) {
            int normalCount = ThreadLocalRandom.current().nextInt(2, 4);
            int babyCount = ThreadLocalRandom.current().nextInt(0, 2);
            int maskedCount = ThreadLocalRandom.current().nextInt(0, 2);
            for (int i = 0; i < normalCount; i++) {
                entities.add(new OrcNormal());
            }
            for (int i = 0; i < babyCount; i++) {
                entities.add(new OrcBaby());
            }
            for (int i = 0; i < maskedCount; i++) {
                entities.add(new OrcMasked());
            }
        } else {
            int normalCount = ThreadLocalRandom.current().nextInt(2, 4);
            int babyCount = ThreadLocalRandom.current().nextInt(1, 4);
            int maskedCount = ThreadLocalRandom.current().nextInt(1, 3);
            for (int i = 0; i < normalCount; i++) {
                entities.add(new OrcNormal());
            }
            for (int i = 0; i < babyCount; i++) {
                entities.add(new OrcBaby());
            }
            for (int i = 0; i < maskedCount; i++) {
                entities.add(new OrcMasked());
            }
        }
    }

    /** Used to spawn a ghost and the corresponding tombstone based on chance */
    private void spawnGhost() {
        int rando = ThreadLocalRandom.current().nextInt(0, 5);
        if (rando == 4) {
            Ghost ghost = new Ghost();
            entities.add(ghost);
            entities.add(tomb = new Tombstone(ghost));
            hasGhost = true;
        } else {
            hasGhost = false;
        }
    }

    /** Used to spawn a few traps based on chance */
    private void spawnTraps() {
        int slowCount = ThreadLocalRandom.current().nextInt(0, 3);
        boolean spawnerBool = ThreadLocalRandom.current().nextBoolean();
        for (int i = 0; i < slowCount; i++) {
            new SlowTrap();
        }
        if (spawnerBool) {
            SpawnerTrap spawner = new SpawnerTrap();
            new TrapSwitch(spawner);
        }
    }

    /** Deletes the savefile if possible */
    public static void deleteSave() {
        try {
            Files.deleteIfExists(Paths.get("savefile\\Save.ser"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameOverMenu<Actor> getGameOverMenu() {
        return gameOverMenu;
    }

    public static int getLevelCount() {
        return levelCount;
    }

    public static void setLevelCount(int levelCount) {
        Game.levelCount = levelCount;
    }

    public boolean isHasGhost() {
        return hasGhost;
    }

    public void setHasGhost(boolean hasGhost) {
        this.hasGhost = hasGhost;
    }

    public void setTomb(Tombstone tomb) {
        this.tomb = tomb;
    }
}
