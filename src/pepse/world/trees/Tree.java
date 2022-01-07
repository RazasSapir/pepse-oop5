package pepse.world.trees;

import danogl.GameObject;
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



    private static void createTree(Vector2 location, int tree_height, GameObjectCollection gameObjects, int layer){
        // create tree in wanted location and height
        for(int idx = 1; tree_height >= idx; idx++)
        {
            gameObjects.addGameObject(new Block(new Vector2(location.x(), location.y() - Block.SIZE * idx),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_TREE_COLOR))), layer);
        }
    }

    private static void creaLeafCollection(Vector2 topTreeLocation, int tree_height,
                                           GameObjectCollection gameObjects, int layer){
        // create number of leaves in relation to the tree height
        // add cols to the tree
        float baseXValue = topTreeLocation.x() - (tree_height / 2) * Block.SIZE;
        float currXValue = 0;
        float currYValue = 0;
        for(int row = 0; tree_height > row; row++){
            currXValue = baseXValue + row * Block.SIZE;
            for(int col = 0; (tree_height / 2) + 1 > col; col++){
                currYValue = topTreeLocation.y() + Block.SIZE - col *Block.SIZE;
                gameObjects.addGameObject(
                        new Leaf(new Vector2(currXValue, currYValue), new Vector2(Block.SIZE, Block.SIZE),
                                new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR))), layer);
            }
        }
    }

    public static boolean createInRange(int minX, int maxX, Terrain terrain, GameObjectCollection gameObjects, int layer,
                                     int liffLayer) {
        // create random values in the wanted range
        minX = (int) (Math.floor((float) minX / Block.SIZE) + 1) * Block.SIZE;
        maxX = (int) (Math.floor((float) maxX / Block.SIZE) + 1) * Block.SIZE;
        Random value_number = new Random(Objects.hash(minX, SEED));

        int curr_height = 0;
        boolean createdLeaves = false;
        for(int curr_value = minX; maxX > curr_value; curr_value += Block.SIZE){
            // add tree in probability of 1 / 10
            if(value_number.nextInt() % 10 == 1){
                // determine tree height
                curr_height = (Math.abs(value_number.nextInt()) % (MAXHEIGHT - MINHEIGHT + 1)) + MINHEIGHT;
                //create a new tree if necessary
                double height = Math.floor(terrain.groundHeightAt((float) curr_value / Block.SIZE) / Block.SIZE) * Block.SIZE;
                int roundedHeight = Math.max((int) (height - (height % Block.SIZE)), 0);
                createTree(new Vector2(curr_value, roundedHeight),
                        curr_height, gameObjects, layer);
                creaLeafCollection(new Vector2(curr_value, roundedHeight - Block.SIZE * curr_height),
                        curr_height, gameObjects, liffLayer);
                createdLeaves = true;
            }
        }
        return createdLeaves;
    }
}