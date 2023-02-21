package com.diamantino.spacerevolution.client.screen;

import com.diamantino.spacerevolution.client.screen.handlers.CrusherScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CrusherScreen extends BaseMachineScreen<CrusherScreenHandler> {
    public CrusherScreen(CrusherScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, 176, 174, inventory, title, "crusher_gui.png");
    }

    @Override
    void drawScreenContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderProgressArrow(matrices, bgX, bgY);
    }

    private void renderProgressArrow(MatrixStack matrices, int x, int y) {
        if (handler.isCrafting()) {
            drawTexture(matrices, x + 77, y + 32, 176, 0, handler.getScaleProgress(22), 26);
        }
    }
}
