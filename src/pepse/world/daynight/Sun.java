package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.lang.Math;
import java.awt.*;

public class Sun {
    private static final Color BASIC_SUN_COLOR = Color.YELLOW;
    private static final String SUN_TAG = "sun";
    private static final Vector2 SUN_SIZE = new Vector2(100, 100);
    private static Vector2 middleScreen;


    /**
     * Updates the suns location based on the given angle.
     * @param curr_angle float - angle to position the object in.
     * @param curr_obj object to move.
     */
    private static void setNewCenter(float curr_angle, GameObject curr_obj){
        Vector2 new_place = middleScreen.add(new Vector2((float)Math.sin(Math.toRadians(curr_angle)),
                (float)(-1 * Math.cos(Math.toRadians(curr_angle)))).mult(middleScreen.y()));
        curr_obj.setCenter(new_place);
    }

    /**
     * create a Sun GameObject - A yellow circle with a transition of circling the camera.
     * @param gameObjects Collection of the game's objects
     * @param layer int layer to add the Sun object to.
     * @param windowDimensions Vector2 size of the window
     * @param cycleLength float time length of a full day.
     * @return GameObject of the created Sun Object.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE, new OvalRenderable(BASIC_SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        middleScreen = new Vector2(windowDimensions.x() / 2, windowDimensions.y());
        new Transition<Float>(
                sun, // the game object being changed
               (curr_angle) -> setNewCenter(curr_angle, sun), // the method to call
                0f, // initial transition value
                360f, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP,
                null); // nothing further to execute upon reaching final value
        return sun;
    }
}