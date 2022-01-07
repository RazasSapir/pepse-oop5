package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManger;

import java.awt.event.KeyEvent;

/**
 * class to manage the Avatar object.
 * It is in charge of presenting the avatar and update it's position and state throughout the game
 * according to the user's choice
 * @authors Raz Sapir and Ari Lehavi
 */
public class Avatar extends GameObject {
    public static final int INITIAL_ENERGY = 100;
    public static final double ENERGY_DELTA = 0.5;
    private final UserInputListener inputListener;
    private final Renderable imageStandingRenderer;
    private final AnimationRenderable animationRanderer;
    private double energy;
    private boolean isFlying;

    private static final Vector2 PLAYER_DIMENSIONS = new Vector2(28, 49);
    private static final String PLAYER_IMAGE_STANDING = "pepse/assets/player_standing.png";
    private static final String[] PLAYER_WALKING = new String[]{"pepse/assets/player_left.png",
            "pepse/assets/player_right.png"};

    /**
     * Constructor for the Avatar GameObject
     * @param pos Vector2 initial position
     * @param size Vector2 Object's size
     * @param avatarStandingRenderer Renderer for the avatar while standing
     * @param animatedRenderer Renderer for the avatar while walking
     * @param inputListener InputLister the handle the avatar's movements.
     */
    public Avatar(Vector2 pos, Vector2 size, Renderable avatarStandingRenderer, AnimationRenderable animatedRenderer, UserInputListener inputListener) {
        super(pos, size, avatarStandingRenderer);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(PepseGameManger.GRAVITY);
        this.inputListener = inputListener;
        this.imageStandingRenderer = avatarStandingRenderer;
        this.animationRanderer = animatedRenderer;
        this.energy = INITIAL_ENERGY;
        this.isFlying = false;
    }

    /**
     * Helper Function the create the avatar
     * @param gameObjects GameObjectCollection to add terrain blocks to
     * @param layer int layer to put the avatar in
     * @param topLeftCorner Vector2 initial position of the avatar
     * @param inputListener UserInputListener to handle the movement of the avatar based on user input
     * @param imageReader ImageReader for rendering the avatar's look
     * @return the newly created avatar GameObject
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                      int layer, Vector2 topLeftCorner,
                                      UserInputListener inputListener,
                                      ImageReader imageReader) {
        ImageRenderable avatarStanding = imageReader.readImage(PLAYER_IMAGE_STANDING, false);
        AnimationRenderable avatarWalking = new AnimationRenderable(PLAYER_WALKING, imageReader, false, 0.25);
        Avatar avatar = new Avatar(topLeftCorner, PLAYER_DIMENSIONS, avatarStanding, avatarWalking, inputListener);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * Updates the avatar's location based on userInput
     * @param deltaTime time between frames
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            xVel -= PepseGameManger.VELOCITY_X;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            xVel += PepseGameManger.VELOCITY_X;
        transform().setVelocityX(xVel);
        handle_renderer();
        this.isFlying = false;
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) {
            this.isFlying = true;
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
            this.energy -= ENERGY_DELTA;
        }
        else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0)
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
        else if (getVelocity().x() == 0 && getVelocity().y() == 0 && energy < 100) {
            this.energy += ENERGY_DELTA;
        }
    }

    /**
     * @return Energy level of the avatar
     */
    public double getEnergy(){
        return energy;
    }

    /**
     * Handles the direction and type of the render based on the direction of the avatar.
     */
    private void handle_renderer() {
        // Handle flying mode
        if (isFlying) {
            renderer().setRenderableAngle(-90);
            renderer().setRenderable(imageStandingRenderer);
        }
        else {
            renderer().setRenderableAngle(0);
            renderer().setRenderable(animationRanderer);
        }
        // Handle Walking mode
        if (getVelocity().x() == 0){
            renderer().setRenderable(imageStandingRenderer);
        }
        else{
            if (getVelocity().x() > 0 && renderer().isFlippedHorizontally())
                renderer().setIsFlippedHorizontally(false);
            else if (getVelocity().x() < 0 && !renderer().isFlippedHorizontally())
                    renderer().setIsFlippedHorizontally(true);
        }
    }
}
