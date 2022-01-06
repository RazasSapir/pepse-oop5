package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {
    public static final String GROUND_BLOCK = "Ground Block";
    public static final int INTERACTIVE_DEPTH = 2;
    private final double groundDeltaFactor;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final double groundHeightAtX0;
    private final NoiseGenerator NoiseGenerator;
    private static final int TERRAIN_DEPTH = 30;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);


    public Terrain(GameObjectCollection gameObjects,
                   Vector2 windowDimensions, int groundLayer,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * 2 / 3;
        this.groundDeltaFactor = windowDimensions.y();
        this.NoiseGenerator = new NoiseGenerator(seed);
    }

    public double groundHeightAt(float x) {
        return groundHeightAtX0 + groundDeltaFactor * this.NoiseGenerator.noise(x);
    }

    public void createInRange(int minX, int maxX) {
        minX = (int) (Math.floor((float) minX / Block.SIZE) + 1) * Block.SIZE;
        maxX = (int) (Math.floor((float) maxX / Block.SIZE) + 1) * Block.SIZE;
        int counter = 0;
        for (int i = minX; i < maxX; i += Block.SIZE) {
            double height = Math.floor(groundHeightAt((float) i / Block.SIZE) / Block.SIZE) * Block.SIZE;
            int roundedHeight = Math.max((int) (height - (height % Block.SIZE)), 0); // Make sure there is at least one block
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                RectangleRenderable groundBlockRenderer = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, roundedHeight + j * Block.SIZE), groundBlockRenderer);
                if (j < INTERACTIVE_DEPTH)
                    this.gameObjects.addGameObject(block, this.groundLayer);
                else
                    this.gameObjects.addGameObject(block, Layer.BACKGROUND);
                block.setTag(GROUND_BLOCK);
                counter += 1;
            }
        }
    }
}
