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
import pepse.util.EnergyDisplay;

import pepse.util.Animal;
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

/**
 * class to manage the entire game.
 * It is in charge of updating the world in which the avatar is on throughout the game.
 * @authors Raz Sapir and Ari Lehavi
 */
public class PepseGameManger extends GameManager {

    public static final int SEED = 42;
    public static final float GRAVITY = 500;
    public static final float VELOCITY_X = 300;
    public static final float VELOCITY_Y = -300;
    private static final String ANIMAL_IMAGE_STANDING = "pepse/assets/animal1_standing.png";
    private static final String[] ANIMAL_WALKING = new String[]{"pepse/assets/animal1_left.png",
            "pepse/assets/animal1_right.png"};
    private static final float PADDING = 5;
    private static final float TEXT_SIZE = 30;
    private static final int treeLayer = Layer.STATIC_OBJECTS + 1;
    private static final int groundLayer = Layer.STATIC_OBJECTS;
    private static final int liffLayer = Layer.STATIC_OBJECTS + 2;
    private static final int avatarLayer = Layer.DEFAULT;
    private static final int MAX_ANIMALS_IN_RANGE = 3;
    private static final int SAFETY_GAP = 200;
    private static final Vector2 ANIMAL_DIMENSIONS =  new Vector2(Block.SIZE, Block.SIZE);
    private static final Color BASIC_SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    public static final int BASIC_ENERGY_LAYER = Layer.BACKGROUND + 20;


    private ImageReader imageReader;
    private Terrain terrain;
    private int currentScreen;
    private Avatar avatar;
    private float screenSize;
    private float farLeftBoundary;
    private float leftBoundary;
    private float rightBoundary;
    private float farRightBoundary;
    private EnergyDisplay energyDisplay;
    public static final int DAY_LENGTH = 30;
    private boolean createdCollision = false;
    private Tree tree;

    /**
     * Constructor for the PepseGameManger
     *
     * @param windowTitle      String title of the window
     * @param windowDimensions Vector2 Size of the wanted Game Window
     */
    public PepseGameManger(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * Updates the boundaries of the screen based on the currentScreen field
     */
    private void updateBoundaries() {
        farLeftBoundary = (int) ((currentScreen - 1) * screenSize);
        leftBoundary = (int) ((currentScreen) * screenSize);
        rightBoundary = (int) ((currentScreen + 1) * screenSize);
        farRightBoundary = (int) ((currentScreen + 2) * screenSize);
    }

    /**
     * Updates the games based on the position's of the avatar
     * @param deltaTime time between frames
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        energyDisplay.update();
        if (avatar.getCenter().x() < currentScreen * screenSize){
            currentScreen -= 1;
            updateBoundaries();
            terrain.createInRange((int) farLeftBoundary, (int) leftBoundary);
            this.tree.createInRange((int) farLeftBoundary, (int) leftBoundary);
            // remove irrelevant objects
            createAnimalsInRange((int)farLeftBoundary, (int) leftBoundary, terrain, this.gameObjects(), avatarLayer, this.imageReader);
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() > roundToBlock(farRightBoundary));
        }
        else if (avatar.getCenter().x() > (currentScreen + 1) * screenSize){
            currentScreen += 1;
            updateBoundaries();
            terrain.createInRange((int) rightBoundary, (int) farRightBoundary);
            this.tree.createInRange((int) rightBoundary, (int) farRightBoundary);

            // remove the trees and irrelevant terrain
            createAnimalsInRange((int)rightBoundary, (int) farRightBoundary, terrain, this.gameObjects(), avatarLayer, this.imageReader);
            // remove irrelevant objects
            removeObjectsByCondition(g -> g.getTopLeftCorner().x() < roundToBlock(farLeftBoundary));
        }
        if (!createdCollision) {
            try{
                gameObjects().layers().shouldLayersCollide(liffLayer, groundLayer, true);
                gameObjects().layers().shouldLayersCollide(treeLayer, avatarLayer, true);
                createdCollision = true;
            }
            catch(java.util.NoSuchElementException ignored){
            }
        }
    }

    /**
     * Helper function to remove the objects far away from the player
     * makes the program run smoother
     * @param condition Predicate<GameObject> the check if the given GameObject should be deleted
     */
    private void removeObjectsByCondition(Predicate<GameObject> condition) {
        ArrayList<GameObject> tempToRemove = new ArrayList<>();
        for (GameObject g : gameObjects()) {
            if (condition.test(g) && (g instanceof Block || g instanceof Leaf || g instanceof Animal)) {
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

    /**
     * Override initializeGame - inits the different objects
     * @param imageReader      ImageReader for rendering Images
     * @param soundReader      SoundReader for rendering sound
     * @param inputListener    UserInputListener to handle user's input
     * @param windowController WindowController to control the game's window
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.currentScreen = 0;
        Vector2 windowDimensions = windowController.getWindowDimensions();
        this.screenSize = windowDimensions.x();
        this.imageReader = imageReader;
        // Init Sky
        Sky.create(this.gameObjects(), windowDimensions, Layer.BACKGROUND);
        // Init Terrain
        terrain = new Terrain(this.gameObjects(), groundLayer, windowDimensions, SEED);
        terrain.createInRange((int) -screenSize, (int) (2 * screenSize));
        // Init trees
        this.tree = new Tree(terrain, this.gameObjects(), treeLayer,liffLayer);
        InitTrees();
        // Init Avatar
        Vector2 avatarPosition = new Vector2(windowDimensions.x() * 0.5F, 0);
        this.avatar = Avatar.create(gameObjects(), avatarLayer, avatarPosition, inputListener, imageReader);


        createAnimalsInRange((int) -screenSize, 0, terrain, this.gameObjects(), avatarLayer, imageReader);
        createAnimalsInRange(0, (int) screenSize, terrain, this.gameObjects(), avatarLayer, imageReader);
        createAnimalsInRange((int) screenSize, (int)(2 * screenSize), terrain, this.gameObjects(), avatarLayer, imageReader);



        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        // Init Energy Display
        this.energyDisplay = new EnergyDisplay(
                new Vector2(PADDING, PADDING),
                new Vector2(TEXT_SIZE, TEXT_SIZE), avatar::getEnergy);
        gameObjects().addGameObject(energyDisplay.getEnergyText(), BASIC_ENERGY_LAYER);
        // Init night, sun and sun halo
        Night.create(this.gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        GameObject sun = Sun.create(this.gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), DAY_LENGTH);
        SunHalo.create(this.gameObjects(), Layer.BACKGROUND + 1, sun, BASIC_SUN_HALO_COLOR);
    }

    /**
     * Helper function to initialize the trees
     */
    private void InitTrees() {
        this.tree.createInRange((int) -screenSize, 0);
        this.tree.createInRange(0, (int) screenSize);
        this.tree.createInRange((int) screenSize, (int) (2 * screenSize));
        // Create collision between the layers only if they're aren't empty
        try{
            gameObjects().layers().shouldLayersCollide(liffLayer, groundLayer, true);
            gameObjects().layers().shouldLayersCollide(treeLayer, avatarLayer, true);
            createdCollision = true;
        }
        catch(java.util.NoSuchElementException ignored){
        }
    }

    /**
     * Helper function to round x value to Block.SIZE
     * @param x float value to round
     * @return rounder int value
     */
    public static int roundToBlock(float x){
        return (int) (Math.floor(x / Block.SIZE) + 1) * Block.SIZE;
    }

    /**
     * helper function to create a random ammount of animals in a certain area in the game.
     * @param minX the minimal x value of the area to create the animals in
     * @param maxX the maximal x value of the area to create the animals in
     * @param terrain the terrain the animals are on
     * @param gameObjects the game object list of the game
     * @param layer the layer to draw the animals in
     * @param imageReader the imageReader of the game
     */
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
            gameObjects.addGameObject(new Animal(new Vector2(curr_X, (roundedHeight - ANIMAL_DIMENSIONS.y())),
                    ANIMAL_DIMENSIONS, animalStanding, animalWalking, SEED, minX, maxX), layer);
        }

    }
}
