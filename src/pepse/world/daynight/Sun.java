package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.lang.Math;
import java.awt.*;

public class Sun {
    private static final Color BASIC_SUN_COLOR = Color.YELLOW;
    private static final String SUN_TAG = "sun";
    private static final Vector2 SUN_SIZE = new Vector2(300, 300);
    private static Vector2 MIDDLE_SCREEN = new Vector2(0,0);


    private static void setNewCenter(float curr_angle, GameObject curr_obj){
        Vector2 new_place = MIDDLE_SCREEN.add(new Vector2((float)Math.sin(Math.toRadians(curr_angle)),
                (float)(-1 * Math.cos(Math.toRadians(curr_angle)))).mult(MIDDLE_SCREEN.y()));
        curr_obj.setCenter(new_place);
    }
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE, new OvalRenderable(BASIC_SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        MIDDLE_SCREEN = new Vector2(windowDimensions.x() / 2, windowDimensions.y());
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