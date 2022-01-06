package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;

import java.util.ArrayList;
import java.util.function.Predicate;

public class PepseGameManger extends GameManager {

    public static final int SEED = 42;
    public static final float GRAVITY = 500;
    public static final float VELOCITY_X = 300;
    public static final float VELOCITY_Y = -300;
    private static final String PLAYER_IMAGE = "assets/player_image.png";
    private static final Vector2 PLAYER_DIMENSIONS = new Vector2(28, 50);
    private UserInputListener inputListener;
    private Terrain terrain;
    private Vector2 windowDimensions;
    private int currentScreen;
    private Avatar avatar;
    private float screenSize;
    private float farLeftBoundary;
    private float leftBoundary;
    private float rightBoundary;
    private float farRightBoundary;

    public PepseGameManger() {

    }

    public PepseGameManger(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    private void updateBoundaries(){
        farLeftBoundary = (int) ((currentScreen - 1) * screenSize);
        leftBoundary = (int) ((currentScreen) * screenSize);
        rightBoundary = (int) ((currentScreen + 1) * screenSize);
        farRightBoundary = (int) ((currentScreen + 2) * screenSize);}

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (avatar.getCenter().x() < currentScreen * screenSize){
            currentScreen -= 1;
            updateBoundaries();
            terrain.createInRange((int) farLeftBoundary, (int) leftBoundary);
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() > roundToBlock(farRightBoundary));
        }
        else if (avatar.getCenter().x() > (currentScreen + 1) * screenSize){
            currentScreen += 1;
            updateBoundaries();
            terrain.createInRange((int) rightBoundary, (int) farRightBoundary);
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() < roundToBlock(farLeftBoundary));
        }
    }

    private void removeObjectsByCondition(Predicate<GameObject> condition) {
        ArrayList<GameObject> tempToRemove = new ArrayList<>();
        for (GameObject g : gameObjects()) {
            if (condition.test(g) && g instanceof Block) {
                tempToRemove.add(g);
            }
        }
        for (GameObject g : tempToRemove) {
            gameObjects().removeGameObject(g, Layer.STATIC_OBJECTS);
            gameObjects().removeGameObject(g, Layer.BACKGROUND);
        }
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.inputListener = inputListener;
        this.currentScreen = 0;
        this.windowDimensions = windowController.getWindowDimensions();
        this.screenSize = windowDimensions.x();
        Sky.create(this.gameObjects(), windowDimensions, Layer.BACKGROUND);
        terrain = new Terrain(this.gameObjects(), windowDimensions, Layer.STATIC_OBJECTS, SEED);
        terrain.createInRange((int) -screenSize, (int) (2 * screenSize));
        Vector2 avatarPosition = new Vector2(windowDimensions.x() * 0.5F, 0);
        this.avatar = createAvatar(gameObjects(), Layer.DEFAULT, avatarPosition, inputListener, imageReader);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
    }

    public static Avatar createAvatar(GameObjectCollection gameObjects,
                                      int layer, Vector2 topLeftCorner,
                                      UserInputListener inputListener,
                                      ImageReader imageReader){
        ImageRenderable avatarRenderer = imageReader.readImage(PLAYER_IMAGE, true);
        Avatar avatar = new Avatar(topLeftCorner, PLAYER_DIMENSIONS, avatarRenderer, inputListener);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    public static int roundToBlock(float x){
        return (int) (Math.floor(x / Block.SIZE) + 1) * Block.SIZE;
    }
}
