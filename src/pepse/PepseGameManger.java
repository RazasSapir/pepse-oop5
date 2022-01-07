package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.util.Animal;
import pepse.util.EnergyDisplay;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.awt.*;

import java.util.ArrayList;
import java.util.function.Predicate;

public class PepseGameManger extends GameManager {

    public static final int SEED = 42;
    public static final float GRAVITY = 500;
    public static final float VELOCITY_X = 300;
    public static final float VELOCITY_Y = -300;
    private static final String PLAYER_IMAGE_STANDING = "assets/player_standing.png";
    private static final String[] PLAYER_WALKING = new String[]{"assets/player_left.png",
            "assets/player_right.png"};
    private static final String ANIMAL_IMAGE_STANDING = "assets/animal1_standing.png";
    private static final String[] ANIMAL_WALKING = new String[]{"assets/animal1_left.png",
            "assets/animal1_right.png"};
    private static final Vector2 PLAYER_DIMENSIONS = new Vector2(28, 50);
    private static final float PADDING = 5;
    private static final float TEXT_SIZE = 30;
    private static final int treeLayer = Layer.STATIC_OBJECTS + 1;
    private static final int groundLayer = Layer.STATIC_OBJECTS;
    private static final int liffLayer = Layer.STATIC_OBJECTS + 2;
    private static final int avatarLayer = Layer.DEFAULT;
    private static final int MAX_ANIMALS_IN_RANGE = 3;
    private static final int SAFETY_GAP = 200;
    private static final int Y_GAP = 150;
    private static final Vector2 ANIMAL_DIMENSIONS =  new Vector2(Block.SIZE, Block.SIZE);



    private UserInputListener inputListener;
    private ImageReader imageReader;
    private Terrain terrain;
    private Vector2 windowDimensions;
    private int currentScreen;
    private Avatar avatar;
    private float screenSize;
    private float farLeftBoundary;
    private float leftBoundary;
    private float rightBoundary;
    private float farRightBoundary;
    private EnergyDisplay energyDisplay;
    public static final int DAY_LENGTH = 30;
    private static final Color BASIC_SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private boolean createdCollision = false;


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
        boolean needToCreateCollision = false;
        energyDisplay.update();
        if (avatar.getCenter().x() < currentScreen * screenSize){
            currentScreen -= 1;
            updateBoundaries();
            terrain.createInRange((int) farLeftBoundary, (int) leftBoundary);
            needToCreateCollision = Tree.createInRange((int) farLeftBoundary, (int) leftBoundary, terrain, this.gameObjects(),
                    treeLayer, liffLayer);
            createAnimalsInRange((int)farLeftBoundary, (int) leftBoundary, terrain, this.gameObjects(), avatarLayer, this.imageReader);
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() > roundToBlock(farRightBoundary));
        }
        else if (avatar.getCenter().x() > (currentScreen + 1) * screenSize){
            currentScreen += 1;
            updateBoundaries();
            terrain.createInRange((int) rightBoundary, (int) farRightBoundary);
            needToCreateCollision = Tree.createInRange((int) rightBoundary, (int) farRightBoundary, terrain, this.gameObjects(),
                    treeLayer, liffLayer);

            // remove the trees and irrelevant terrain
            createAnimalsInRange((int)rightBoundary, (int) farRightBoundary, terrain, this.gameObjects(), avatarLayer, this.imageReader);
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() < roundToBlock(farLeftBoundary));
        }
        if(!createdCollision && needToCreateCollision){
            gameObjects().layers().shouldLayersCollide(liffLayer, groundLayer, true);
            gameObjects().layers().shouldLayersCollide(treeLayer, avatarLayer, true);
            createdCollision = true;
        }
    }

    private void removeObjectsByCondition(Predicate<GameObject> condition) {
        ArrayList<GameObject> tempToRemove = new ArrayList<>();
        for (GameObject g : gameObjects()) {
            if (condition.test(g) && (g instanceof Block || g instanceof Leaf)) {
                tempToRemove.add(g);
            }
        }
        for (GameObject g : tempToRemove) {
            gameObjects().removeGameObject(g, treeLayer);
            gameObjects().removeGameObject(g, Layer.STATIC_OBJECTS);
            gameObjects().removeGameObject(g, liffLayer);
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
        this.imageReader = imageReader;
        Sky.create(this.gameObjects(), windowDimensions, Layer.BACKGROUND);
        terrain = new Terrain(this.gameObjects(), windowDimensions, groundLayer, SEED);
        terrain.createInRange((int) -screenSize, (int) (2 * screenSize));

        boolean create1 = Tree.createInRange((int) -screenSize, (int) 0, terrain, this.gameObjects(), treeLayer,
                liffLayer);
        boolean create2 = Tree.createInRange((int) 0, (int) screenSize, terrain, this.gameObjects(), treeLayer,
                liffLayer);
        boolean create3 = Tree.createInRange((int) screenSize, (int)(2 * screenSize), terrain, this.gameObjects(), treeLayer,
                liffLayer);


        Vector2 avatarPosition = new Vector2(windowDimensions.x() * 0.5F, 0);
        this.avatar = createAvatar(gameObjects(), avatarLayer, avatarPosition, inputListener, imageReader);

        // create collision between the layers only if they're aren't empty
        if(create1 || create2 || create3){
            gameObjects().layers().shouldLayersCollide(liffLayer, groundLayer, true);
            gameObjects().layers().shouldLayersCollide(treeLayer, avatarLayer, true);
            createdCollision = true;
        }

        createAnimalsInRange((int) -screenSize, (int) 0, terrain, this.gameObjects(), avatarLayer, imageReader);
        createAnimalsInRange((int) 0, (int) screenSize, terrain, this.gameObjects(), avatarLayer, imageReader);
        createAnimalsInRange((int) screenSize, (int)(2 * screenSize), terrain, this.gameObjects(), avatarLayer, imageReader);



        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        this.energyDisplay = new EnergyDisplay(
                new Vector2(PADDING, PADDING),
                new Vector2(TEXT_SIZE, TEXT_SIZE), () -> avatar.getEnergy());
        gameObjects().addGameObject(energyDisplay.getEnergyText(), Layer.BACKGROUND);
        Night.create(this.gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        GameObject sun = Sun.create(this.gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        SunHalo.create(this.gameObjects(), Layer.BACKGROUND + 1, sun, BASIC_SUN_HALO_COLOR);
    }

    public static Avatar createAvatar(GameObjectCollection gameObjects,
                                      int layer, Vector2 topLeftCorner,
                                      UserInputListener inputListener,
                                      ImageReader imageReader){
        ImageRenderable avatarStanding = imageReader.readImage(PLAYER_IMAGE_STANDING, false);
        AnimationRenderable avatarWalking = new AnimationRenderable(PLAYER_WALKING, imageReader, false, 0.25);
        Avatar avatar = new Avatar(topLeftCorner, PLAYER_DIMENSIONS, avatarStanding, avatarWalking, inputListener);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    public static void createAnimalsInRange(int minX, int maxX, Terrain terrain, GameObjectCollection gameObjects, int layer,
                                                ImageReader imageReader){
        int curr_animals_number = (int)(Math.random() * MAX_ANIMALS_IN_RANGE);
        ImageRenderable animalStanding = imageReader.readImage(ANIMAL_IMAGE_STANDING, false);
        AnimationRenderable animalWalking = new AnimationRenderable(ANIMAL_WALKING, imageReader, false, 0.25);
        for(int counter = 0; curr_animals_number > counter; counter++)
        {
            int curr_X = (int)(Math.random() * (maxX - minX - SAFETY_GAP + 1) + minX + SAFETY_GAP);
            double height = Math.floor(terrain.groundHeightAt((float) curr_X / Block.SIZE) / Block.SIZE) * Block.SIZE;
            float roundedHeight = Math.max((int) (height - (height % Block.SIZE)), 0);
            gameObjects.addGameObject(new Animal(new Vector2(curr_X, (float)(roundedHeight - ANIMAL_DIMENSIONS.y())),
                    ANIMAL_DIMENSIONS, animalStanding, animalWalking, SEED, minX, maxX), layer);
        }

    }

    public static int roundToBlock(float x){
        return (int) (Math.floor(x / Block.SIZE) + 1) * Block.SIZE;
    }
}
