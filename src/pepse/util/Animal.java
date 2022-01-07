package pepse.util;

import danogl.GameObject;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManger;

import java.util.Random;

public class Animal extends GameObject {
    private final Renderable imageStandingRenderer;
    private final AnimationRenderable animationRenderer;
    private int xVel;
    private final Random randomizer;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param avatarStandingRenderer    The renderable representing the object. Can be null, in which case
     */
    public Animal(Vector2 topLeftCorner, Vector2 dimensions, Renderable avatarStandingRenderer, AnimationRenderable animatedRenderer, long seed) {
        super(topLeftCorner, dimensions, avatarStandingRenderer);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(PepseGameManger.GRAVITY);
        this.imageStandingRenderer = avatarStandingRenderer;
        this.randomizer = new Random(seed);
        this.animationRenderer = animatedRenderer;
        this.xVel = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean shouldMoveHorizontally = false;
        boolean shouldJump = false;
        if (randomizer.nextFloat() < 0.1)
            shouldMoveHorizontally = true;
        if (randomizer.nextFloat() < 0.3)
            shouldJump = true;
        if (shouldMoveHorizontally)
            xVel = randomizer.nextInt(500);
        transform().setVelocityX(xVel);
        if (shouldJump) {
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
        }
        handle_renderer();
    }












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
