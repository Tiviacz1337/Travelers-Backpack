package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundMemoryPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MemoryWidget extends WidgetBase
{
    public MemoryWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = true;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TravelersBackpackScreen.SETTTINGS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            blit(matrixStack, x, y, 16, isWidgetActive ? 19 : 0, width, height);
        }
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if(isHovered && showTooltip)
        {
            String[] s =  I18n.get("screen.travelersbackpack.memory").split("\n");
            screen.renderComponentTooltip(poseStack, List.of(new TextComponent(s[0]), new TextComponent(s[1])), mouseX, mouseY);
        }
    }

    @Override
    public void setWidgetStatus(boolean status)
    {
        super.setWidgetStatus(status);
        screen.sortWidget.setTooltipVisible(!status);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(screen.container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            return false;
        }

        if(isHovered)
        {
            setWidgetStatus(!this.isWidgetActive);

            //Turns slot checking on server
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.MEMORY, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            TravelersBackpack.NETWORK.sendToServer(new ServerboundMemoryPacket(screen.container.getScreenID(), screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY), screen.container.getSlotManager().getMemorySlots().stream().mapToInt(Pair::getFirst).toArray(), screen.container.getSlotManager().getMemorySlots().stream().map(Pair::getSecond).toArray(ItemStack[]::new)));
            screen.container.getSlotManager().setSelectorActive(SlotManager.MEMORY, !screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY));

            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public int[] getWidgetSizeAndPos()
    {
        int[] size = new int[4];
        size[0] = x;
        size[1] = y;
        size[2] = width;
        size[3] = height;
        return size;
    }
}