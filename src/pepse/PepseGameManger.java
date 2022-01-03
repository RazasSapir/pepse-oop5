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

public class PepseGameManger extends GameManager {
    public PepseGameManger(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sky.create(this.gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        new Terrain(this.gameObjects(), Layer.DEFAULT, windowController.getWindowDimensions(), 50).createInRange(0, (int) windowController.getWindowDimensions().x());
    }
}
