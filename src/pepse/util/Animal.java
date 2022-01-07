package pepse.util;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManger;
import java.util.Random;

/**
 * class to manage the Animal object that appears on the screen.
 * It is in charge of presenting the animals and moving it automatically around the screen
 * @authors Raz Sapir and Ari Lehavi
 */
public class Animal extends GameObject {
    private final Renderable imageStandingRenderer;
    private final AnimationRenderable animationRenderer;
    private final int SAFETY_GAP = 200;
    private static final int MAX_X_VEL = 100;
    private int xVel;
    private final Random randomizer;
    private static final float HORIZONTAL_THRESHOLD = 0.3f;
    private static final float VERTICAL_THRESHOLD = 0.5f;


    private int minX;
    private int maxX;
    private boolean onFly = false;
    private boolean onMovementHorizontal = false;
    private int onMovementHorizontalCounter = 50;
    private int onFlyCounter = 20;

    /**
     * Construct a new GameObject instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param animalStandingRenderer A renderer representing the animal when standign
     * @param animatedRenderer An animation renderer for animal
     * @param seed a seed for the randomizer object.
     */
    public Animal(Vector2 topLeftCorner, Vector2 dimensions, Renderable animalStandingRenderer, AnimationRenderable animatedRenderer, long seed,
                  int minX, int maxX) {
        super(topLeftCorner, dimensions, animalStandingRenderer);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(PepseGameManger.GRAVITY);
        this.imageStandingRenderer = animalStandingRenderer;
        this.randomizer = new Random(seed);
        this.animationRenderer = animatedRenderer;
        this.xVel = 0;
        this.minX = minX;
        this.maxX = maxX;
    }

    /**
     * Updated the movement of the animal
     * @param deltaTime time between frames.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean shouldMoveHorizontally = false;
        boolean shouldJump = false;
        if(onFly){
            if(this.transform().getVelocity().y() < 0 && this.onFlyCounter == 0) {
                this.transform().setVelocityY((float) -1 * this.getVelocity().y());
                this.onFlyCounter = 20;
            }
            else{
                this.onFlyCounter -= 1;
            }
        }

        if(onMovementHorizontal){
            if(this.onMovementHorizontalCounter == 0){
                onMovementHorizontal = false;
                this.onMovementHorizontalCounter = 100;
                this.transform().setVelocityX(0);
            }
            else{
                this.onMovementHorizontalCounter -= 1;
            }
        }

        if (randomizer.nextFloat() < HORIZONTAL_THRESHOLD){
            shouldMoveHorizontally = true;
        }
        if (randomizer.nextFloat() < VERTICAL_THRESHOLD)
            shouldJump = true;

        if (shouldMoveHorizontally && !onMovementHorizontal){
            xVel = (int)((Math.random() * (2*MAX_X_VEL)) - MAX_X_VEL);
            transform().setVelocityX(xVel);
            onMovementHorizontal = true;
        }

        if (shouldJump && !onFly) {
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
            onFly = true;
        }

        // put the animal in the wanted range
        if((this.getCenter().x() < this.minX + SAFETY_GAP) && this.getVelocity().x() < 0){
            this.setVelocity(new Vector2(-1 * this.getVelocity().x(), this.getVelocity().y()));
        }

        if((this.getCenter().x() > this.maxX - SAFETY_GAP) && this.getVelocity().x() > 0){
            this.setVelocity(new Vector2(-1 * this.getVelocity().x(), this.getVelocity().y()));
        }

        handle_renderer();
    }

    /**
     * handle the collision of an animal with the surface.
     * in case it landed on the ground, it's y velocity turns to 0.
     * @param other object to collide with
     * @param collision the collision style
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(onFly && this.transform().getVelocity().y() > 0){
            onFly = false;
            this.transform().setVelocityY(0);
            this.onFlyCounter = 20;
        }
    }

    /**
     * Handles the direction and type of the render based on the direction of the animal.
     */
    private void handle_renderer() {
        if (getVelocity().x() == 0){
            renderer().setRenderable(imageStandingRenderer);
        }
        else{
            renderer().setRenderable(animationRenderer);
            if (getVelocity().x() > 0 && renderer().isFlippedHorizontally())
                renderer().setIsFlippedHorizontally(false);
            else if (getVelocity().x() < 0 && !renderer().isFlippedHorizontally())
                renderer().setIsFlippedHorizontally(true);
        }
    }
}
