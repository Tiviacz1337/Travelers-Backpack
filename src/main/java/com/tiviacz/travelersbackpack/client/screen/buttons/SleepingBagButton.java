package com.tiviacz.travelersbackpack.client.screen.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.util.math.MatrixStack;

public class SleepingBagButton extends Button
{
    public SleepingBagButton(TravelersBackpackHandledScreen screen)
    {
        super(screen, 5, 42 + screen.inventory.getYOffset(), 18, 18);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if(screen.inventory.hasTileEntity() && !screen.toolSlotsWidget.isCoveringButton())
        {
            this.drawButton(matrices, mouseX, mouseY, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, 19, 0, 0, 0);

            //Fill the bed icon with the color of the sleeping bag
            RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK);
            screen.drawTexture(matrices, screen.getX() + x + 1, screen.getY() + y + 1, getBedIconX(screen.inventory.getSleepingBagColor()), getBedIconY(screen.inventory.getSleepingBagColor()), 16, 16);
        }
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(screen.inventory.hasTileEntity() && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(this.inButton((int) mouseX, (int) mouseY) && !screen.isWidgetVisible(3, screen.leftTankSlotWidget))
            {
                ClientPlayNetworking.send(ModNetwork.DEPLOY_SLEEPING_BAG_ID, PacketByteBufs.create().writeBlockPos(screen.inventory.getPosition()));
                return true;
            }
        }
        return false;
    }

    public int getBedIconX(int colorId)
    {
        return 1 + (colorId <= 7 ? 0 : 19);
    }

    public int getBedIconY(int colorId)
    {
        return 19 + ((colorId % 8) * 17);
    }
}