package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;


import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Tree {
    public static final int SEED = 42;
    public static final int MINHEIGHT = 3;
    public static final int MAXHEIGHT = 6;
    public static final Color BASE_TREE_COLOR = new Color(100, 50, 20);
    public static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);

    private Terrain terrain;
    private GameObjectCollection gameObjects;
    private int layer;
    private int leavesLayer;

    /**
     * constructor for the Tree object.
     * @param terrain the terrain of the game's world.
     * @param gameObjects the list of game objects in the game.
     * @param layer the layer the trees are created in.
     * @param leavesLayer the layer the leaves are created in.
     */
    public Tree(Terrain terrain, GameObjectCollection gameObjects, int layer,
                 int leavesLayer)
    {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.leavesLayer = leavesLayer;
    }

    /**
     * Creates a tree in given location and height
     * @param location Vector 2 for the bottom of the tree
     * @param tree_height int height of the wanted tree
     * @param gameObjects Collection of the game's objects
     * @param layer int layer to add the Tree object to.
     */
    private static void createTree(Vector2 location, int tree_height, GameObjectCollection gameObjects, int layer){
        for(int idx = 1; tree_height >= idx; idx++)
        {
            gameObjects.addGameObject(new Block(new Vector2(location.x(), location.y() - Block.SIZE * idx),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_TREE_COLOR))), layer);
        }
    }

    /**
     *
     * @param topTreeLocation Vector 2 the location of the top of the tree's trunk
     * @param tree_height height of the tree's trunk
     * @param gameObjects Collection of the game's objects
     * @param layer int layer to add the Leaves objects to.
     */
    private static void createLeafCollection(Vector2 topTreeLocation, int tree_height,
                                             GameObjectCollection gameObjects, int layer){
        float baseXValue = topTreeLocation.x() - (tree_height / 2.0F) * Block.SIZE;
        for(int row = 0; tree_height > row; row++){
            float currXValue = baseXValue + row * Block.SIZE;
            for(int col = 0; (tree_height / 2) + 1 > col; col++){
                float currYValue = topTreeLocation.y() + Block.SIZE - col * Block.SIZE;
                gameObjects.addGameObject(
                        new Leaf(new Vector2(currXValue, currYValue), new Vector2(Block.SIZE, Block.SIZE),
                                new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR))), layer);
            }
        }
    }

    /**
     * Creates Trees in the given range
     * @param minX int minimum x value
     * @param maxX int maximum x value
     */
    public void createInRange(int minX, int maxX) {
        // create random values in the wanted range
        minX = (int) (Math.floor((float) minX / Block.SIZE) + 1) * Block.SIZE;
        maxX = (int) (Math.floor((float) maxX / Block.SIZE) + 1) * Block.SIZE;
        Random value_number = new Random(Objects.hash(minX, SEED));

        boolean createdLeaves = false;
        for(int curr_value = minX; maxX > curr_value; curr_value += Block.SIZE){
            // add tree in probability of 1 / 10
            if(value_number.nextInt() % 10 == 1){
                // determine tree height
                int curr_height = (Math.abs(value_number.nextInt()) % (MAXHEIGHT - MINHEIGHT + 1)) + MINHEIGHT;
                //create a new tree if necessary
                double height = Math.floor(this.terrain.groundHeightAt((float) curr_value / Block.SIZE) / Block.SIZE) * Block.SIZE;
                int roundedHeight = Math.max((int) (height - (height % Block.SIZE)), 0);
                createTree(new Vector2(curr_value, roundedHeight),
                        curr_height, this.gameObjects, layer);
                createLeafCollection(new Vector2(curr_value, roundedHeight - Block.SIZE * curr_height),
                        curr_height, gameObjects, leavesLayer);
                createdLeaves = true;
            }
        }
    }
}