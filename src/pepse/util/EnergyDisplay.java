package pepse.util;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;

import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;
import danogl.util.Vector2;

/**
 * class to manage the representation of the energy levels of the avatar.
 * It is in charge of presenting the energy level and changing it on the screen
 * @authors Raz Sapir and Ari Lehavi
 */
public class EnergyDisplay {

    public static final String ENERGY_PREFACE = "Energy: ";
    private final TextRenderable textRenderable;
    private final GameObject energyText;
    private Double currEnergy;
    private final Supplier<Double> getEnergy;

    /**
     * Constructor of the numeric counter of the player's lives.
     * @param topLeftCorner vector for the left corner of the text "[number of lives]"
     * @param dimensions of the text object.
     * @param getEnergy Supplier for the energy level of the player
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Supplier<Double> getEnergy) {
        TextRenderable energyTextRenderer = new TextRenderable(ENERGY_PREFACE + getEnergy.get(), Font.SERIF);
        energyText = new GameObject(topLeftCorner, dimensions, energyTextRenderer);
        energyText.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        currEnergy = getEnergy.get();
        this.getEnergy = getEnergy;
        this.textRenderable = energyTextRenderer;
    }

    /**
     * @return GameObject of the Energy Text.
     */
    public GameObject getEnergyText(){
        return energyText;
    }

    /**
     * Update the energy Value.
     */
    public void update(){
        if (!Objects.equals(currEnergy, getEnergy.get())){
            textRenderable.setString(ENERGY_PREFACE + getEnergy.get());
            currEnergy = getEnergy.get();
        }
    }
}
