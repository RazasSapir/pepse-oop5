package pepse.world.daynight;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.util.Vector2;
import danogl.gui.rendering.RectangleRenderable;
import danogl.components.CoordinateSpace;

import java.awt.*;


public class Night {

    private static final Color BASIC_NIGHT_COLOR = Color.decode("#000000");
    private static final String NIGHT_TAG = "night";
    private static final Float MIDNIGHT_OPACITY = 0.5f;


    /**
     * create a Night GameObject - A black screen with fading in and out animations.
     * @param gameObjects Collection of the game's objects
     * @param layer int layer to add the Night object to. Should probably be Layer.FOREGROUND.
     * @param windowDimensions Vector2 size of the window
     * @param cycleLength float time length of a full day.
     * @return GameObject of the created Night Object.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, new RectangleRenderable(BASIC_NIGHT_COLOR));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        new Transition<Float>(
                night, // the game object being changed
                night.renderer()::setOpaqueness, // the method to call
                0f, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength/2, // transition fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        return night;
    }
}
