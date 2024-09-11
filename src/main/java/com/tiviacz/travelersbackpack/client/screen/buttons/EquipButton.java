package com.tiviacz.travelersbackpack.client.screen.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class EquipButton extends Button
{
    public EquipButton(TravelersBackpackHandledScreen screen)
    {
        super(screen, 5, 42 + screen.inventory.getYOffset(), 18, 18);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if(!screen.inventory.hasTileEntity())
        {
            if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && !screen.toolSlotsWidget.isCoveringButton())
            {
                this.drawButton(context, mouseX, mouseY, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, 57, 0, 38, 0);
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY)
    {
        if(TravelersBackpack.enableIntegration() && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID)
            {
                if(this.inButton(mouseX, mouseY))
                {
                    context.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.equip_integration"), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!screen.inventory.hasTileEntity())
        {
            if(!TravelersBackpack.enableIntegration())
            {
                if(!ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
                {
                    if(this.inButton((int) mouseX, (int) mouseY))
                    {
                        //PacketByteBuf buf = PacketByteBufs.create();
                        //buf.writeBoolean(true);

                        //ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                        ClientPlayNetworking.send(new EquipBackpackPacket(true));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}