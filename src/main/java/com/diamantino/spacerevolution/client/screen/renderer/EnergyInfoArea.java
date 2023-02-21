package com.diamantino.spacerevolution.client.screen.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

/**
 * @author BluSunrize
 */
public class EnergyInfoArea extends InfoArea {
    private final EnergyStorage energy;

    public EnergyInfoArea(int xMin, int yMin)  {
        this(xMin, yMin, null,8,64);
    }

    public EnergyInfoArea(int xMin, int yMin, EnergyStorage energy)  {
        this(xMin, yMin, energy,8,64);
    }

    public EnergyInfoArea(int xMin, int yMin, EnergyStorage energy, int width, int height)  {
        super(new Rect2i(xMin, yMin, width, height));
        this.energy = energy;
    }

    public List<Text> getTooltips() {
        return List.of(Text.literal(energy.getAmount() + "/" + energy.getCapacity() + " GE"));
    }

    @Override
    public void draw(MatrixStack transform) {
        final int height = area.getHeight();
        int stored = (int) (height * (energy.getAmount() / (float) energy.getCapacity()));

        fillGradient(transform, area.getX(), area.getY() + (height - stored), area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0xFF4B00FF, 0xFF1100FF);
    }
}