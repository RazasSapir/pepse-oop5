package pepse.world;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManger;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    public static final int INITIAL_ENERGY = 100;
    public static final double ENERGY_DELTA = 0.5;
    private final UserInputListener inputListener;
    private double energy;

    public Avatar(Vector2 pos, Vector2 size, Renderable avatarRenderer, UserInputListener inputListener) {
        super(pos, size, avatarRenderer);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(PepseGameManger.GRAVITY);
        this.inputListener = inputListener;
        this.energy = INITIAL_ENERGY;
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
        handle_renderer_direction();
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) {
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
            this.energy -= ENERGY_DELTA;
        }
        else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0)
            transform().setVelocityY(PepseGameManger.VELOCITY_Y);
        else if (getVelocity().x() == 0 && getVelocity().y() == 0) {
            this.energy += ENERGY_DELTA;
        }
    }

    private void handle_renderer_direction() {
        if (getVelocity().x() > 0 && !renderer().isFlippedHorizontally())
            renderer().setIsFlippedHorizontally(true);
        else if (getVelocity().x() < 0 && renderer().isFlippedHorizontally())
            renderer().setIsFlippedHorizontally(false);
    }
}
