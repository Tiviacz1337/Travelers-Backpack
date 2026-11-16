package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;

public class AnimatedTank {
    private static final float DURATION_MS = 150.0F;

    private float displayedHeight = -1;
    private float startHeight;
    private float targetHeight;
    private long startTime;
    private FluidVariantWrapper displayedFluid = FluidVariantWrapper.blank();

    public void render(GuiGraphicsExtractor guiGraphics, FluidVariantWrapper fluid, long amount, long capacity, int x, int y, int height, int width) {
        float newTarget = getTargetHeight(fluid.isEmpty() ? 0 : amount, capacity, height);
        long now = Util.getMillis();

        if(this.displayedHeight < 0) {
            this.displayedHeight = newTarget;
            this.startHeight = newTarget;
            this.targetHeight = newTarget;
        } else if(newTarget != this.targetHeight) {
            this.startHeight = this.displayedHeight;
            this.targetHeight = newTarget;
            this.startTime = now;
        }

        float progress = Math.min((now - this.startTime) / DURATION_MS, 1.0F);
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        this.displayedHeight = this.startHeight + (this.targetHeight - this.startHeight) * eased;

        if(!fluid.isEmpty()) {
            this.displayedFluid = fluid.copy();
        }
        if(this.displayedHeight <= 0 || this.displayedFluid.isEmpty()) {
            return;
        }
        RenderHelper.renderSmoothScreenTank(guiGraphics, this.displayedFluid, this.displayedHeight, x, y, height, width);
    }

    public void reset() {
        this.displayedHeight = -1;
        this.displayedFluid = FluidVariantWrapper.blank();
    }

    private static float getTargetHeight(long amount, long capacity, int height) {
        if(amount <= 0 || capacity <= 0) {
            return 0;
        }
        return (int)Math.max(Math.min(height, (double)amount * height / capacity), 1);
    }
}
