package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * class to manage the "block" object.
 * It is in charge of creating a new block (a cube of a certain size and color)
 * @authors Raz Sapir and Ari Lehavi
 */
public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * Constructor for the terrain block Object
     * @param topLeftCorner Vector 2 block position
     * @param renderable Renderer for the terrain block object
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

}
