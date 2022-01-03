package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.zip.ZipEntry;

public class Terrain {
    public static final String GROUND_BLOCK = "Ground Block";
    private static final double GROUND_DELTA_FACTOR = 2 * Block.SIZE;
    private final GameObjectCollection gameObjects;
    private final int groudLayer;
    private final double groundHeightAtX0;
    private final NoiseGenerator NoiseGenerator;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final float groundYBase;


    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groudLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * 1 / 3;
        this.groundYBase = windowDimensions.y();
        this.NoiseGenerator = new NoiseGenerator(seed);
    }

    public double groundHeightAt(float x) {
        return groundHeightAtX0 + GROUND_DELTA_FACTOR * this.NoiseGenerator.noise(x);
    }

    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX ; i+= Block.SIZE) {
            double height = groundHeightAt(i);
            int roundedHeight = (int)(height - (height % Block.SIZE));
            for (int j = 0; j < roundedHeight; j+= Block.SIZE) {
                RectangleRenderable groundBlockRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, groundYBase - j), groundBlockRenderable);
                this.gameObjects.addGameObject(block, this.groudLayer);
                block.setTag(GROUND_BLOCK);
            }
        }
    }
}
