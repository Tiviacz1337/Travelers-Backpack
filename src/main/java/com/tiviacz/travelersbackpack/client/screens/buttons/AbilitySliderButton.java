package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class AbilitySliderButton extends Button
{
    public AbilitySliderButton(TravelersBackpackScreen screen)
    {
        super(screen, 5, screen.container.getRows() <= 4 ? 26 : 56, 18, 11);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if(screen.container.hasBlockEntity())
        {
            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, screen.container.getItemStack()))
            {
                if(!screen.toolSlotsWidget.isCoveringAbility())
                {
                    drawButton(guiGraphics, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);
                }
            }
        }
        else
        {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, screen.container.getItemStack()))
                {
                    if(!screen.toolSlotsWidget.isCoveringAbility())
                    {
                        drawButton(guiGraphics, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);
                    }
                }
            }
        }
    }

    public void drawButton(GuiGraphics guiGraphics, int mouseX, int mouseY, ResourceLocation texture)
    {
        if(screen.container.getAbilityValue())
        {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 114, 0, 95, 0);
        }
        else
        {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 114, 12, 95, 12);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if(screen.container.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID || screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, screen.container.getItemStack()) && this.inButton(mouseX, mouseY) && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.isWidgetVisible(4, screen.leftTankSlotWidget))
            {
                if(!screen.toolSlotsWidget.isCoveringAbility())
                {
                    if(screen.container.getAbilityValue())
                    {
                        List<FormattedCharSequence> list = new ArrayList<>();
                        list.add(Component.translatable("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                        if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, screen.container.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, screen.container.getItemStack()))
                        {
                            list.add(screen.container.getLastTime() == 0 ? Component.translatable("screen.travelersbackpack.ability_ready").getVisualOrderText() : Component.translatable(BackpackUtils.getConvertedTime(screen.container.getLastTime())).getVisualOrderText());
                        }
                        guiGraphics.renderTooltip(screen.getFont(), list, mouseX, mouseY);
                    }
                    else
                    {
                        if(!TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() || !BackpackAbilities.ALLOWED_ABILITIES.contains(screen.container.getItemStack().getItem()))
                        {
                            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                        }
                        else
                        {
                            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(screen.container.hasBlockEntity())
        {
            if(!screen.toolSlotsWidget.isCoveringAbility())
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, screen.container.getItemStack()) && this.inButton((int)mouseX, (int)mouseY) && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.isWidgetVisible(4, screen.leftTankSlotWidget))
                {
                    PacketDistributor.SERVER.noArg().send(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()));
                    //TravelersBackpack.NETWORK.send(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()), PacketDistributor.SERVER.noArg());
                    screen.playUIClickSound();
                    return true;
                }
            }
        }
        else if(screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            if(!screen.toolSlotsWidget.isCoveringAbility())
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, screen.container.getItemStack()) && this.inButton((int)mouseX, (int)mouseY) && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.isWidgetVisible(4, screen.leftTankSlotWidget))
                {
                    PacketDistributor.SERVER.noArg().send(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()));
                    //TravelersBackpack.NETWORK.send(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()), PacketDistributor.SERVER.noArg());
                    screen.playUIClickSound();
                    return true;
                }
            }
        }
        return false;
    }
}