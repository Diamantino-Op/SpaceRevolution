package com.diamantino.spacerevolution.client.screen;

import com.diamantino.spacerevolution.client.screen.handlers.BaseMachineScreenHandler;
import com.diamantino.spacerevolution.client.screen.renderer.EnergyInfoArea;
import com.diamantino.spacerevolution.client.screen.renderer.FluidStackRenderer;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.storage.FluidStorage;
import com.diamantino.spacerevolution.utils.MouseUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseMachineScreen<S extends BaseMachineScreenHandler> extends HandledScreen<S> {
    private static Identifier backgroundTexture;

    int bgX;
    int bgY;

    EnergyInfoArea energyInfoArea;
    public List<FluidStackRenderer> fluidStackRenderers;

    public BaseMachineScreen(S handler, int width, int height, PlayerInventory inventory, Text title, String backgroundTextureName) {
        super(handler, inventory, title);

        backgroundTexture = new Identifier(ModReferences.modId, "textures/gui/" + backgroundTextureName);

        backgroundWidth = width;
        backgroundHeight = height;
    }

    @Override
    protected void init() {
        super.init();

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        playerInventoryTitleY += 9;

        bgX = (width - backgroundWidth) / 2;
        bgY = (height - backgroundHeight) / 2;

        assignEnergyInfoArea();
        assignFluidStackRenderer();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyInfoArea(bgX + 156, bgY + 13, handler.blockEntity.energyStorage.getSideStorage(Direction.SOUTH));
    }

    private void assignFluidStackRenderer() {
        fluidStackRenderers = new ArrayList<>(handler.blockEntity.fluidStorages.getSize());

        for (int i = 0; i < handler.blockEntity.fluidStorages.getSize(); i++) {
            SingleVariantStorage<FluidVariant> storage = handler.blockEntity.fluidStorages.getStorage(i);
            FluidStorage.FluidTankDimensions dimensions = handler.blockEntity.fluidStorages.getStorageDimensions(i);

            fluidStackRenderers.add(i, new FluidStackRenderer(storage.getCapacity(), true, dimensions.width, dimensions.height));
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        renderEnergyAreaTooltips(matrices, mouseX, mouseY, bgX, bgY);
        renderFluidTooltips(matrices, mouseX, mouseY, bgX, bgY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, backgroundTexture);
        drawTexture(matrices, bgX, bgY, 0, 0, backgroundWidth, backgroundHeight);

        drawScreenContent(matrices, delta, mouseX, mouseY);

        energyInfoArea.draw(matrices);

        int i = 0;
        for (FluidStackRenderer renderer : fluidStackRenderers) {
            FluidStorage.FluidTankDimensions dimensions = handler.blockEntity.fluidStorages.getStorageDimensions(i);

            renderer.drawFluid(matrices, handler.fluidStacks.get(i), bgX + dimensions.x, bgY + dimensions.y, dimensions.width, dimensions.height, renderer.capacityMb);

            i++;
        }
    }

    void drawScreenContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

    private void renderEnergyAreaTooltips(MatrixStack matrices, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 156, 13, 8, 64)) {
            renderTooltip(matrices, energyInfoArea.getTooltips(), Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderFluidTooltips(MatrixStack matrices, int pMouseX, int pMouseY, int x, int y) {
        int i = 0;
        for (FluidStackRenderer renderer : fluidStackRenderers) {
            FluidStorage.FluidTankDimensions dimensions = handler.blockEntity.fluidStorages.getStorageDimensions(i);

            if(isMouseAboveArea(pMouseX, pMouseY, x, y, dimensions.x, dimensions.y, renderer)) {
                renderTooltip(matrices, renderer.getTooltip(handler.fluidStacks.get(i), TooltipContext.Default.BASIC), Optional.empty(), pMouseX - x, pMouseY - y);
            }

            i++;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);

        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        return MouseUtils.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtils.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
