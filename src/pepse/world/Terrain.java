package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

/**
 * class to manage the Terrain object.
 * It is in charge of presenting the terrain throughout the game and changing the surface when needed
 * @authors Raz Sapir and Ari Lehavi
 */
public class Terrain {
    public static final String GROUND_BLOCK = "Ground Block";
    public static final int INTERACTIVE_DEPTH = 2;
    private final float groundDeltaFactor;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final NoiseGenerator NoiseGenerator;
    private static final int TERRAIN_DEPTH = 30;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int BASIC_TERRAIN_LAYER = Layer.BACKGROUND + 10;

    /**
     * Constructor for the terrain Handler
     * @param gameObjects GameObjectCollection to add terrain blocks to
     * @param windowDimensions Vector2 size of the window
     * @param groundLayer int the layer in which terrain blocks should be positioned.
     * @param seed long for randomizing the terrain height
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        double BASIC_GROUND_FACTOR = 2.0 / 3.0;
        this.groundHeightAtX0 = (float) (windowDimensions.y() * BASIC_GROUND_FACTOR);
        this.groundDeltaFactor = windowDimensions.y();
        this.NoiseGenerator = new NoiseGenerator(seed);
    }

    /**
     * @param x float value to check the ground's height at
     * @return height of the ground at position x - not rounded to block's size
     */
    public float groundHeightAt(float x) {
        return groundHeightAtX0 + groundDeltaFactor * (float) this.NoiseGenerator.noise(x);
    }

    /**
     * Creates Terrain in the given range
     * @param minX int minimum x value
     * @param maxX int maximum x value
     */
    public void createInRange(int minX, int maxX) {
        minX = (int) (Math.floor((float) minX / Block.SIZE) + 1) * Block.SIZE;
        maxX = (int) (Math.floor((float) maxX / Block.SIZE) + 1) * Block.SIZE;
        for (int i = minX; i < maxX; i += Block.SIZE) {
            double height = Math.floor(groundHeightAt((float) i / Block.SIZE) / Block.SIZE) * Block.SIZE;
            int roundedHeight = Math.max((int) (height - (height % Block.SIZE)), 0); // Make sure there is at least one block
            // Add terrain colum
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                RectangleRenderable groundBlockRenderer = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, roundedHeight + j * Block.SIZE), groundBlockRenderer);
                if (j < INTERACTIVE_DEPTH)
                    this.gameObjects.addGameObject(block, this.groundLayer);
                else
                    this.gameObjects.addGameObject(block, BASIC_TERRAIN_LAYER);
                block.setTag(GROUND_BLOCK);
            }
        }
    }
}
