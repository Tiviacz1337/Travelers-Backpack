package com.tiviacz.travelersbackpack.client.screen.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;

public class EquipButton extends Button
{
    public EquipButton(TravelersBackpackHandledScreen screen)
    {
        super(screen, 5, 42 + screen.inventory.getYOffset(), 18, 18);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if(!screen.inventory.hasTileEntity())
        {
            if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && !screen.toolSlotsWidget.isCoveringButton())
            {
                this.drawButton(matrices, mouseX, mouseY, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, 57, 0, 38, 0);
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY)
    {
        if(TravelersBackpack.enableTrinkets() && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID)
            {
                if(this.inButton(mouseX, mouseY))
                {
                    screen.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.equip_integration"), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!screen.inventory.hasTileEntity())
        {
            if(!TravelersBackpack.enableTrinkets())
            {
                if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
                {
                    if(this.inButton((int) mouseX, (int) mouseY))
                    {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBoolean(true);

                        ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}