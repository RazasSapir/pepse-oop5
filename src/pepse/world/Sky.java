package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class to manage the Sky object.
 * It is in charge of presenting the sky in the beginning of the game statically
 * @authors Raz Sapir and Ari Lehavi
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky";

    /**
     * This method adds a Sky gameObject to the given GameObjectCollection and returns it
     * @param gameObjects GameObjectCollection to add sky to
     * @param windowDimensions Vector2 defines the size of the sky
     * @param skyLayer int the layer in which sky should be positioned.
     * @return GameObject of the newly added sky.
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
