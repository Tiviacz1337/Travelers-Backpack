package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class AbilitySliderButton extends Button
{
    public AbilitySliderButton(TravelersBackpackScreen screen)
    {
        super(screen, 5, screen.container.getRows() <= 4 ? 26 : 56, 18, 11);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if(screen.container.hasBlockEntity())
        {
            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, screen.container.getItemStack()))
            {
                if(!screen.toolSlotsWidget.isCoveringAbility())
                {
                    drawButton(poseStack, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);
                }
            }
        }
        else
        {
            if(CapabilityUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, screen.container.getItemStack()))
                {
                    if(!screen.toolSlotsWidget.isCoveringAbility())
                    {
                        drawButton(poseStack, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);
                    }
                }
            }
        }
    }

    public void drawButton(PoseStack poseStack, int mouseX, int mouseY, ResourceLocation texture)
    {
        if(screen.container.getAbilityValue())
        {
            this.drawButton(poseStack, mouseX, mouseY, texture, 114, 0, 95, 0);
        }
        else
        {
            this.drawButton(poseStack, mouseX, mouseY, texture, 114, 12, 95, 12);
        }
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
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
                        list.add(new TranslatableComponent("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                        if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, screen.container.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, screen.container.getItemStack()))
                        {
                            list.add(screen.container.getLastTime() == 0 ? new TranslatableComponent("screen.travelersbackpack.ability_ready").getVisualOrderText() : new TranslatableComponent(BackpackUtils.getConvertedTime(screen.container.getLastTime())).getVisualOrderText());
                        }
                        screen.renderTooltip(poseStack, list, mouseX, mouseY);
                    }
                    else
                    {
                        if(!TravelersBackpackConfig.enableBackpackAbilities || !BackpackAbilities.ALLOWED_ABILITIES.contains(screen.container.getItemStack().getItem()))
                        {
                            screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                        }
                        else
                        {
                            screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
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
                    TravelersBackpack.NETWORK.sendToServer(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()));
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
                    TravelersBackpack.NETWORK.sendToServer(new ServerboundAbilitySliderPacket(screen.container.getScreenID(), !screen.container.getAbilityValue()));
                    screen.playUIClickSound();
                    return true;
                }
            }
        }
        return false;
    }
}