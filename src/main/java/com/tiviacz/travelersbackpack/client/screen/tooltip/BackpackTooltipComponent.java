package com.tiviacz.travelersbackpack.client.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(value= EnvType.CLIENT)
public class BackpackTooltipComponent implements TooltipComponent
{
    public static final Identifier LEATHER_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/leather_travelers_backpack_tooltip.png");
    public static final Identifier IRON_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/iron_travelers_backpack_tooltip.png");
    public static final Identifier GOLD_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/gold_travelers_backpack_tooltip.png");
    public static final Identifier DIAMOND_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/diamond_travelers_backpack_tooltip.png");
    public static final Identifier NETHERITE_TOOLTIP_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/tooltip/netherite_travelers_backpack_tooltip.png");
    private final BackpackTooltipData component;

    public BackpackTooltipComponent(BackpackTooltipData component)
    {
        this.component = component;
    }

    @Override
    public int getHeight()
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasNbt())
        {
            return getTextureHeight();
        }
        return 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer)
    {
        if(BackpackUtils.isCtrlPressed() && component.stack.hasNbt())
        {
            return 229;
        }
        return 0;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
    {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context)
    {
        if(!component.stack.hasNbt()) return;

        if(BackpackUtils.isCtrlPressed())
        {
            blit(context, x, y);
            int slot = 0;
            boolean isEmpty = true;

            if(!component.inventory.isEmpty())
            {
                for(int j = 0; j < 3 + component.tier.getOrdinal(); j++)
                {
                    for(int i = 0; i < 9; i++)
                    {
                        if(applyGridConditions(i, j)) continue;

                        int i1 = x + i * 18 + 43;
                        int j1 = y + j * 18 + 6;
                        this.renderItemInSlot(i1, j1, slot, textRenderer, context, false);
                        slot++;
                    }
                }
                isEmpty = false;
            }

            int craftingSlot = 0;

            if(!component.craftingInventory.isEmpty())
            {
                for(int j = 0; j < 3; j++)
                {
                    for(int i = 0; i < 3; i++)
                    {
                        int i1 = x + i * 18 + 151;
                        int j1 = y + j * 18 + (component.tier.getOrdinal() * 18) + 6;
                        this.renderItemInSlot(i1, j1, craftingSlot, textRenderer, context, true);
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
                        this.renderItemInSlot(x + 5, y + (tool * 18) + 6, i, textRenderer, context, false);
                        tool++;
                    }
                }
            }

            if(!component.leftTank.isResourceBlank())
            {
                RenderUtils.renderScreenTank(context, component.leftTank, x + 25, y + 7, 1000, component.tier.getTankRenderPos(), 16);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if(!component.rightTank.isResourceBlank())
            {
                RenderUtils.renderScreenTank(context, component.rightTank, x + 207, y + 7, 1000, component.tier.getTankRenderPos(), 16);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderItemInSlot(int x, int y, int slot, TextRenderer textRenderer, DrawContext context, boolean isCrafting)
    {
        ItemStack stack = ItemStack.EMPTY;

        if(!isCrafting)
        {
            if(slot > component.tier.getStorageSlots() + component.tier.getToolSlots()) return;

            stack = component.inventory.getStack(slot);
        }
        else
        {
            stack = component.craftingInventory.getStack(slot);
        }

        if(stack.isEmpty()) return;

        context.drawItemWithoutEntity(stack, x + 1, y + 1);
        context.drawItemInSlot(textRenderer, stack, x + 1, y + 1);
    }

    private void blit(DrawContext context, int x, int y)
    {
        context.drawTexture(getTooltipTexture(), x, y, 0, 0, 229, getTextureHeight(), 256, 256);
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

    public Identifier getTooltipTexture()
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