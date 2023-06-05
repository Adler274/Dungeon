package graphic.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import java.util.logging.Logger;
import logging.CustomLogLevel;
import starter.Game;
import tools.Constants;
import tools.Point;

public class HeroSelection<T extends Actor> extends ScreenController<T> {

    private final Logger menuLogger = Logger.getLogger(this.getClass().getSimpleName());

    /** Creates a new GameOverMenu with a new Spritebatch */
    public HeroSelection() {
        this(new SpriteBatch());
    }

    /** Creates a new GameOverMenu with a given Spritebatch */
    public HeroSelection(SpriteBatch batch) {
        super(batch);

        // headline
        ScreenText screenText =
            new ScreenText(
                "Choose a hero",
                new Point(0, 0),
                3,
                new LabelStyleBuilder(FontBuilder.DEFAULT_FONT)
                    .setFontcolor(Color.RED)
                    .build());
        screenText.setFontScale(3);
        screenText.setPosition(
            (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
            (Constants.WINDOW_HEIGHT) / 1.5f + screenText.getHeight(),
            Align.center | Align.bottom);
        add((T) screenText);

        // button for the selection of the Wizard
        ScreenButton wizard =
            new ScreenButton(
                "Wizard",
                new Point(0, 0),
                new TextButtonListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Game.restart();

                        menuLogger.log(CustomLogLevel.INFO, "The Wizard was choosen");
                    }
                },
                new TextButtonStyleBuilder(FontBuilder.DEFAULT_FONT)
                    .setFontColor(Color.WHITE)
                    .setOverFontColor(Color.RED)
                    .build());

        wizard.setPosition(
            (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
            (Constants.WINDOW_HEIGHT) / 2f + screenText.getHeight(),
            Align.center | Align.bottom);

        // button for the selection of the Knight
        ScreenButton knight =
            new ScreenButton(
                "Knight",
                new Point(0, 0),
                new TextButtonListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Game.end();

                        menuLogger.log(CustomLogLevel.INFO, "The Knight was choosen");
                    }
                },
                new TextButtonStyleBuilder(FontBuilder.DEFAULT_FONT)
                    .setFontColor(Color.WHITE)
                    .setOverFontColor(Color.RED)
                    .build());

        knight.setPosition(
            (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
            (Constants.WINDOW_HEIGHT) / 3f + screenText.getHeight(),
            Align.center | Align.bottom);

        //Button for the selection of the Dwarf
        ScreenButton dwarf =
            new ScreenButton(
                "Dwarf",
                new Point(0, 0),
                new TextButtonListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Game.restart();

                        menuLogger.log(CustomLogLevel.INFO, "The Dwarf was choosen");
                    }
                },
                new TextButtonStyleBuilder(FontBuilder.DEFAULT_FONT)
                    .setFontColor(Color.WHITE)
                    .setOverFontColor(Color.RED)
                    .build());

        dwarf.setPosition(
            (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
            (Constants.WINDOW_HEIGHT) / 4f + screenText.getHeight(),
            Align.center | Align.bottom);


        add((T) wizard);
        add((T) knight);
        add((T) dwarf);
        hideMenu();
    }

    /** shows the Menu */
    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));

        menuLogger.log(CustomLogLevel.INFO, "The HeroSelection has been opened.");
    }

    /** hides the Menu */
    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
