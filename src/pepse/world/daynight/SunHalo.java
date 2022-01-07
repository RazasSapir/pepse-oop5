package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class to manage the sun halo object.
 * It is in charge of presenting the sun halo based on the sun's position in every iteration
 * @authors Raz Sapir and Ari Lehavi
 */
public class SunHalo {
    private static final Vector2 HALO_SIZE = new Vector2(200, 200);
    private static final String SUN_HALO_TAG = "sun halo";

    /**
     *
     * @param gameObjects Collection of the game's objects
     * @param layer int layer to add the SunHalo object to.
     * @param sun GameObject representing the sun
     * @return GameObject of the SunHalo
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, GameObject sun, Color color){
        GameObject sunHalo = new GameObject(Vector2.ZERO, HALO_SIZE, new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUN_HALO_TAG);
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
        return sunHalo;
    }

}
