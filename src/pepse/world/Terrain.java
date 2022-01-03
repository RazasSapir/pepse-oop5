package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {
    public static final String GROUND_BLOCK = "Ground Block";
    private final double groundDeltaFactor;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final double groundHeightAtX0;
    private final NoiseGenerator NoiseGenerator;
    private final double GROUND_HEIGHT_FACTOR = 0.33;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final float groundYBase;


    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT_FACTOR;
        this.groundDeltaFactor = windowDimensions.y();
        this.groundYBase = windowDimensions.y();
        this.NoiseGenerator = new NoiseGenerator(seed);
    }

    public double groundHeightAt(float x) {
        System.out.println(this.NoiseGenerator.noise(x));
        return groundHeightAtX0 + groundDeltaFactor * this.NoiseGenerator.noise(x);
    }

    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX ; i+= Block.SIZE) {
            double height = groundHeightAt((float) i / Block.SIZE);
            int roundedHeight = Math.max((int)(height - (height % Block.SIZE)), 0); // Make sure there is at least one block
            for (int j = 0; j <= roundedHeight; j+= Block.SIZE) {
                RectangleRenderable groundBlockRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, groundYBase - j - Block.SIZE), groundBlockRenderable);
                this.gameObjects.addGameObject(block, this.groundLayer);
                block.setTag(GROUND_BLOCK);
            }
        }
    }
}
