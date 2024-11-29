package com.tiviacz.travelersbackpackold.client.screen.buttons;

import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpackold.component.ComponentUtils;
import com.tiviacz.travelersbackpackold.network.EquipBackpackPacket;
import com.tiviacz.travelersbackpackold.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class UnequipButton extends Button
{
    public UnequipButton(TravelersBackpackHandledScreen screen)
    {
        super(screen, 5, 42 + screen.inventory.getYOffset(), 18, 18);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        if(!screen.inventory.hasTileEntity())
        {
            if(ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.toolSlotsWidget.isCoveringButton())
            {
                this.drawButton(context, mouseX, mouseY, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, 57, 19, 38, 19);
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY)
    {
        if(TravelersBackpack.enableIntegration() && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID)
            {
                if(this.inButton(mouseX, mouseY))
                {
                    context.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
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
                if(ComponentUtils.isWearingBackpack(screen.getScreenHandler().playerInventory.player) && screen.inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
                {
                    if(this.inButton((int)mouseX, (int)mouseY))
                    {
                        //PacketByteBuf buf = PacketByteBufs.create();
                        //buf.writeBoolean(false);

                        //ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                        ClientPlayNetworking.send(new EquipBackpackPacket(false));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}