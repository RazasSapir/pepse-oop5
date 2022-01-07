package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;


public class Leaf extends GameObject {
    public static final int LEAF_MAX_LIFE_TIME = 40;
    public static final int FADE_OUT_TIME = 40;
    public static final float MAX_ANGLE_VALUE = 15f;
    public static final int MAX_VELOCITY_X_LEAF = 50;
    public static final int Y_VELOCITY_LEAF = 75;
    public static final int VELOCITY_CYCLE_LENGTH = 3;
    public static final int MAX_DEATH_TIME = 25;
    private boolean onReturn = false;
    private boolean onSmaller = true;
    private final float initWidth;
    private float currLifeTime;
    private Transition<Float> movementTransition;
    private float currDeathTime;  //todo: no one uses this field
    private final Vector2 initPos;
    private ScheduledTask alterAngle;
    private ScheduledTask alterSize;
    private ScheduledTask alterDive;
    private boolean deathMode = false;


    /**
     * Construct a new Leaf instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        initWidth = dimensions.x();
        initPos = topLeftCorner;
        this.currLifeTime = (float) Math.random() * LEAF_MAX_LIFE_TIME;
        this.currDeathTime = (float) Math.random() * MAX_DEATH_TIME;

        // declare two transitions
        this.initLeafScheduledTasks();
    }

    /**
     * Inits the scheduled tasks of the leafs.
     */
    private void initLeafScheduledTasks() {
        this.transform().setVelocity(Vector2.ZERO);

        alterAngle = new ScheduledTask(
                this, (float) (Math.random() * 3f), true, this::alterAngle);
        alterSize = new ScheduledTask(this, (float) (Math.random() * 3f), true, this::alterWidth);
        // declare the dropout phase
        alterDive = new ScheduledTask(
                this, currLifeTime, true, this::diveDown);
    }

    /**
     * Function for the schedule task to alter the angle of the leaves.
     */
    private void alterAngle() {
        if (onReturn) {
            this.renderer().setRenderableAngle(this.renderer().getRenderableAngle() -
                    10f);
            if (this.renderer().getRenderableAngle() < -1 * MAX_ANGLE_VALUE) {
                onReturn = false;
            }
        } else {
            this.renderer().setRenderableAngle(this.renderer().getRenderableAngle() +
                    10f);
            if (this.renderer().getRenderableAngle() > 1 * MAX_ANGLE_VALUE) {
                onReturn = true;
            }
        }
    }

    /**
     * Function for the schedule task to alter the width of the leaves.
     */
    private void alterWidth() {
        if (onSmaller) {
            this.setDimensions(new Vector2(this.getDimensions().x(), this.getDimensions().x() - 2f));
            if (this.getDimensions().x() < 0.8 * initWidth) {
                onSmaller = false;
            }
        } else {
            this.setDimensions(new Vector2(this.getDimensions().x(), this.getDimensions().x() + 2f));
            if (this.getDimensions().x() > initWidth) {
                onSmaller = true;
            }
        }
    }

    /**
     * Restarts the position of the current tree.
     */
    private void restartLeaf() {
        new ScheduledTask(this, currDeathTime, false,
                this::restartLife);
    }

    /**
     * helper function to restart life
     */
    private void restartLife()
    {
        deathMode = false;
        this.setTopLeftCorner(initPos);
        this.renderer().setOpaqueness(1f);
        this.currLifeTime = (float) Math.random() * LEAF_MAX_LIFE_TIME;
        this.currDeathTime = (float) Math.random() * MAX_DEATH_TIME;
        this.initLeafScheduledTasks();
    }

    /**
     * Function for the schedule task to alter the fall of the leaves.
     */
    private void diveDown() {
        this.renderer().fadeOut(FADE_OUT_TIME, this::restartLeaf); // fall and then restart

        this.transform().setVelocityY(Y_VELOCITY_LEAF);
        movementTransition =
                new Transition<>(
                        this, // the game object being changed
                        this.transform()::setVelocityX, // the method to call
                        (float) MAX_VELOCITY_X_LEAF, // initial transition value
                        (float) -1 * MAX_VELOCITY_X_LEAF, // final transition value
                        Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                        VELOCITY_CYCLE_LENGTH, // transtion fully over half a day
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null); // nothing further to execute upon reaching final value
    }

    /**
     * Handles the collisions of the leaf with other GameObjects
     * @param other GameObject other
     * @param collision Collision Object
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.transform().setVelocityY(0);
        this.transform().setVelocityX(0);
        this.removeComponent(movementTransition);
        this.removeComponent(alterSize);
        this.removeComponent(alterAngle);
        this.removeComponent(alterDive);
        deathMode = true;
    }

    /**
     * Updates the leaf's velocity if dead
     * @param deltaTime time between frames.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (deathMode) {
            if (!this.getVelocity().equals(Vector2.ZERO)) {
                this.transform().setVelocity(Vector2.ZERO);
            }
        }
    }
}
