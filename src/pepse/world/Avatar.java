package pepse.world;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManger;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    public static final int INITIAL_ENERGY = 100;
    public static final double ENERGY_DELTA = 0.5;
    private final UserInputListener inputListener;
    private final Renderable imageStandingRenderer;
    private final AnimationRenderable animationRanderer;
    private double energy;
    private boolean isFlying;

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

    public double getEnergy(){
        return energy;
    }

    private void handle_renderer() {
        if (isFlying) {
            renderer().setRenderableAngle(-90);
            renderer().setRenderable(imageStandingRenderer);
        }
        else {
            renderer().setRenderableAngle(0);
            renderer().setRenderable(animationRanderer);
        }
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
