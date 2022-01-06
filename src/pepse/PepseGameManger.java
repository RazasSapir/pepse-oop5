package pepse;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.awt.*;

public class PepseGameManger extends GameManager {

    public static final int SEED = 42;
    public static final int DAY_LENGTH = 30;
    private static final Color BASIC_SUN_HALO_COLOR = new Color(255, 255, 0, 20);


    public PepseGameManger() {

    }
    public PepseGameManger(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sky.create(this.gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        new Terrain(this.gameObjects(), Layer.DEFAULT, windowController.getWindowDimensions(), SEED).createInRange(0, (int) windowController.getWindowDimensions().x());
        Night.create(this.gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        GameObject sun = Sun.create(this.gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        SunHalo.create(this.gameObjects(), Layer.BACKGROUND + 10, sun, BASIC_SUN_HALO_COLOR);
    }
}
