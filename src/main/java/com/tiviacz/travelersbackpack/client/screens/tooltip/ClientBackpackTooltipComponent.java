package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.ContainerUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientBackpackTooltipComponent implements ClientTooltipComponent
{
    public static final ResourceLocation LEATHER_TOOLTIP_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tooltip/leather_travelers_backpack_tooltip.png");
    public static final ResourceLocation IRON_TOOLTIP_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tooltip/iron_travelers_backpack_tooltip.png");
    public static final ResourceLocation GOLD_TOOLTIP_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tooltip/gold_travelers_backpack_tooltip.png");
    public static final ResourceLocation DIAMOND_TOOLTIP_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tooltip/diamond_travelers_backpack_tooltip.png");
    public static final ResourceLocation NETHERITE_TOOLTIP_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tooltip/netherite_travelers_backpack_tooltip.png");
    private final BackpackTooltipComponent component;

    public ClientBackpackTooltipComponent(BackpackTooltipComponent component)
    {
        this.component = component;
    }

    @Override
    public int getHeight()
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasTag())
        {
            return getTextureHeight();
        }
        return 0;
    }

    @Override
    public int getWidth(Font font)
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasTag())
        {
            return 229;
        }
        return 0;
    }

    @Override
    public void renderText(Font pFont, int pMouseX, int pMouseY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource)
    {

    }

    @Override
    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics)
    {
        if(!component.stack.hasTag()) return;

        if(BackpackUtils.isCtrlPressed())
        {
            blit(pGuiGraphics, pX, pY);
            int slot = 0;
            boolean isEmpty = true;

            if(!ContainerUtils.isEmpty(component.inventory))
            {
                for(int j = 0; j < 3 + component.tier.getOrdinal(); j++)
                {
                    for(int i = 0; i < 9; i++)
                    {
                        if(applyGridConditions(i, j)) continue;

                        int i1 = pX + i * 18 + 43;
                        int j1 = pY + j * 18 + 6;
                        this.renderItemInSlot(i1, j1, slot, pFont, pGuiGraphics, false);
                        slot++;
                    }
                }
                isEmpty = false;
            }

            int craftingSlot = 0;

            if(!ContainerUtils.isEmpty(component.craftingInventory))
            {
                for(int j = 0; j < 3; j++)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        int i1 = pX + i * 18 + 151;
                        int j1 = pY + j * 18 + (component.tier.getOrdinal() * 18) + 6;
                        this.renderItemInSlot(i1, j1, craftingSlot, pFont, pGuiGraphics, true);
                        craftingSlot++;
                    }
                }
            }

            int tool = 0;

            if(!isEmpty)
            {
                if(component.hasToolInSlot(Tiers.SlotType.TOOL_FIRST))
                {
                    for(int i = component.tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST); i <= component.tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST) + component.tier.getToolSlots() - 1; i++)
                    {
                        this.renderItemInSlot(pX + 5, pY + (tool * 18) + 6, i, pFont, pGuiGraphics, false);
                        tool++;
                    }
                }
            }

            if(!component.leftTank.isEmpty())
            {
                RenderUtils.renderScreenTank(pGuiGraphics, component.leftTank, pX + 25, pY + 7, 1000, component.tier.getTankRenderPos(), 16);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if(!component.rightTank.isEmpty())
            {
                RenderUtils.renderScreenTank(pGuiGraphics, component.rightTank, pX + 207, pY + 7, 1000, component.tier.getTankRenderPos(), 16);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderItemInSlot(int pX, int pY, int slot, Font pFont, GuiGraphics guiGraphics, boolean isCrafting)
    {
        ItemStack stack = ItemStack.EMPTY;

        if(!isCrafting)
        {
            if(slot > component.tier.getStorageSlots() + component.tier.getToolSlots()) return;

            stack = component.inventory.getStackInSlot(slot);
        }
        else
        {
            stack = component.craftingInventory.getStackInSlot(slot);
        }

        if(stack.isEmpty()) return;

        guiGraphics.renderFakeItem(stack, pX + 1, pY + 1);
        guiGraphics.renderItemDecorations(pFont, stack, pX + 1, pY + 1);
    }

    private void blit(GuiGraphics guiGraphics, int pX, int pY)
    {
        guiGraphics.blit(getTooltipTexture(), pX, pY, 0, 0, 229, getTextureHeight(), 256, 256);
    }

    private boolean applyGridConditions(int i, int j)
    {
        if(component.tier == Tiers.LEATHER)
        {
            if(i > 5) return true;
        }

        if(component.tier == Tiers.IRON)
        {
            if(j > 0 && i > 5) return true;
        }

        if(component.tier == Tiers.GOLD)
        {
            if(j > 1 && i > 5) return true;
        }

        if(component.tier == Tiers.DIAMOND)
        {
            if(j > 2 && i > 5) return true;
        }

        if(component.tier == Tiers.NETHERITE)
        {
            if(j > 3 && i > 5) return true;
        }
        return false;
    }

    public ResourceLocation getTooltipTexture()
    {
        if(component.tier == Tiers.LEATHER) return LEATHER_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.IRON) return IRON_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.GOLD) return GOLD_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.DIAMOND) return DIAMOND_TOOLTIP_TRAVELERS_BACKPACK;
        if(component.tier == Tiers.NETHERITE) return NETHERITE_TOOLTIP_TRAVELERS_BACKPACK;
        return LEATHER_TOOLTIP_TRAVELERS_BACKPACK;
    }

    public int getTextureHeight()
    {
        if(component.tier == Tiers.LEATHER) return 67;
        if(component.tier == Tiers.IRON) return 85;
        if(component.tier == Tiers.GOLD) return 103;
        if(component.tier == Tiers.DIAMOND) return 121;
        if(component.tier == Tiers.NETHERITE) return 139;
        return 67;
    }
}