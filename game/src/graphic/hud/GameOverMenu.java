package graphic.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import starter.Game;
import tools.Constants;
import tools.Point;

public class GameOverMenu<T extends Actor> extends ScreenController<T> {

    /** Creates a new GameOverMEnu with a new Spritebatch */
    public GameOverMenu() {
        this(new SpriteBatch());
    }

    /** Creates a new GameOverMenu with a given Spritebatch */
    public GameOverMenu(SpriteBatch batch) {
        super(batch);

        // headline
        ScreenText screenText =
                new ScreenText(
                        "Game Over",
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

        // restart nutton
        ScreenButton restart =
                new ScreenButton(
                        "Restart Game",
                        new Point(0, 0),
                        new TextButtonListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                Game.restart();
                            }
                        },
                        new TextButtonStyleBuilder(FontBuilder.DEFAULT_FONT)
                                .setFontColor(Color.WHITE)
                                .setOverFontColor(Color.RED)
                                .build());

        restart.setPosition(
                (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
                (Constants.WINDOW_HEIGHT) / 2f + screenText.getHeight(),
                Align.center | Align.bottom);

        // end the game
        ScreenButton quit =
                new ScreenButton(
                        "Quit Game",
                        new Point(0, 0),
                        new TextButtonListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                Game.end();
                            }
                        },
                        new TextButtonStyleBuilder(FontBuilder.DEFAULT_FONT)
                                .setFontColor(Color.WHITE)
                                .setOverFontColor(Color.RED)
                                .build());

        quit.setPosition(
                (Constants.WINDOW_WIDTH) / 2f - screenText.getWidth(),
                (Constants.WINDOW_HEIGHT) / 3f + screenText.getHeight(),
                Align.center | Align.bottom);

        add((T) restart);
        add((T) quit);
        hideMenu();
    }

    /** shows the Menu */
    public void showMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
    }

    /** hides the Menu */
    public void hideMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
    }
}
