package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientBackpackTooltipComponent implements ClientTooltipComponent
{
    private final BackpackTooltipComponent component;

    public ClientBackpackTooltipComponent(BackpackTooltipComponent component)
    {
        this.component = component;
    }

    @Override
    public int getHeight()
    {
        int height = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.leftFluidStack.isEmpty())
            {
                height += 10;
            }

            if(!component.rightFluidStack.isEmpty())
            {
                height += 10;
            }

            if(!component.storage.isEmpty())
            {
                height += (int)(Math.ceil((float)component.storage.size() / 9) * 18);
            }

            if(!component.tools.isEmpty())
            {
                height += 18;
            }
        }
        return height;
    }

    @Override
    public int getWidth(Font font)
    {
        int width = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.storage.isEmpty())
            {
                width += Math.min(component.storage.size(), 9) * 18 + Math.min(component.storage.size(), 9) * 2;
            }
        }
        return width;
    }

    @Override
    public void renderText(Font pFont, int pMouseX, int pMouseY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource)
    {
        if(BackpackUtils.isCtrlPressed())
        {
            int yOffset = 0;

            if(!component.leftFluidStack.isEmpty())
            {
                renderFluidTankTooltip(component.leftFluidStack, pFont, pMouseX, pMouseY, pMatrix, pBufferSource);
                yOffset += 10;
            }

            if(!component.rightFluidStack.isEmpty())
            {
                renderFluidTankTooltip(component.rightFluidStack, pFont, pMouseX, pMouseY + yOffset, pMatrix, pBufferSource);
            }
        }
    }

    public void renderFluidTankTooltip(FluidStack fluidStack, Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource bufferSource)
    {
        Component c = Component.translatable(fluidStack.getTranslationKey());
        Component c1 = Component.literal(": ");
        Component c2 = Component.literal(fluidStack.getAmount() + "mB");

        font.drawInBatch(c, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        font.drawInBatch(c1, (float)mouseX + font.width(c), (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        font.drawInBatch(c2, (float)mouseX + font.width(c) + font.width(c1), (float)mouseY, 5592575, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    @Override
    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics)
    {
        int yOffset = 0;

        if(BackpackUtils.isCtrlPressed())
        {
            if(!component.leftFluidStack.isEmpty())
            {
                yOffset += 10;
            }

            if(!component.rightFluidStack.isEmpty())
            {
                yOffset += 10;
            }

            boolean flag = false;

            if(!component.storage.isEmpty())
            {
                int j = 0;
                flag = true;

                for(int i = 0; i < component.storage.size(); i++)
                {
                    renderItem(component.storage.get(i), pX + j*2 + j*18, pY + yOffset, pFont, pGuiGraphics);

                    if(j < 8)
                    {
                        j++;
                    }
                    else
                    {
                        j = 0;
                        yOffset += 18;
                    }
                }
            }

            if(!component.tools.isEmpty())
            {
                if(flag) yOffset += 18;

                for(int i = 0; i < component.tools.size(); i++)
                {
                    renderItem(component.tools.get(i), pX + (i*18), pY + yOffset, pFont, pGuiGraphics);
                }
            }
        }
    }

    private void renderItem(ItemStack stack, int pX, int pY, Font pFont, GuiGraphics guiGraphics)
    {
        guiGraphics.renderFakeItem(stack, pX, pY);
        guiGraphics.renderItemDecorations(pFont, stack, pX, pY);
    }
}