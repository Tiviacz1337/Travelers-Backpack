package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverlayHandledScreen extends Screen
{
    public MinecraftClient mc;
    public ItemRenderer itemRenderer;
    public Window mainWindow;

    public OverlayHandledScreen()
    {
        super(Text.literal(""));

        this.mc = MinecraftClient.getInstance();
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        this.mainWindow = MinecraftClient.getInstance().getWindow();
    }

    static float animationProgress = 0.0F;

    public void renderOverlay(DrawContext context)
    {
        PlayerEntity player = mc.player;

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> rightFluidStorage = inv.getRightTank();
        SingleVariantStorage<FluidVariant> leftFluidStorage = inv.getLeftTank();

        if(!rightFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(context, rightFluidStorage, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(context, leftFluidStorage, scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(inv.getTier() != null)
        {
            List<ItemStack> tools = getTools(inv.getTier(), inv.getInventory());

            if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)).isEmpty())
            {
                drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)), scaledWidth - 30, scaledHeight - 4);
            }
            if(tools.size() > 1)
            {
                if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2).isEmpty())
                {
                    drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND)  + tools.size() - 2), scaledWidth - 30, scaledHeight + 11);
                }
            }
          /*  KeyBinding key = KeybindHandler.CYCLE_TOOL;
            List<ItemStack> tools = getTools(inv.getTier(), inv.getInventory());

            if(key.isPressed() && tools.size() > 2)
            {
                if(animationProgress < 0.0F)
                {
                    animationProgress += 0.05F;
                }
                for(int i = 0; i < getTools(inv.getTier(), inv.getInventory()).size(); i++)
                {
                    drawItemStack(context, getTools(inv.getTier(), inv.getInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
            }
            else
            {
                if(animationProgress > 0.0F)
                {
                    for(int i = 0; i < getTools(inv.getTier(), inv.getInventory()).size(); i++)
                    {
                        drawItemStack(context, getTools(inv.getTier(), inv.getInventory()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                    }
                    animationProgress -= 0.05F;
                }
                else
                {
                    if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)).isEmpty())
                    {
                        drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)), scaledWidth - 30, scaledHeight - 4);
                    }
                    if(tools.size() > 1)
                    {
                        if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2).isEmpty())
                        {
                            drawItemStack(context, inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND)  + tools.size() - 2), scaledWidth - 30, scaledHeight + 11);
                        }
                    }
                }
            } */
        }

        Identifier id = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        if(player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandStack());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            context.drawTexture(id, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            context.drawTexture(id, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(DrawContext drawContext, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(drawContext, fluidStorage, startX, startY, 0, height, width);
    }

    private void drawItemStack(DrawContext context, ItemStack stack, int x, int y)
    {
        context.drawItem(stack, x, y);
        drawItemInSlot(context, stack, x, y);
    }

    public void drawItemInSlot(DrawContext context, ItemStack stack, int x, int y)
    {
        if(!stack.isEmpty())
        {
            context.getMatrices().push();

            int k;
            int l;

            if(stack.isItemBarVisible())
            {
                int i = stack.getItemBarStep();
                int j = stack.getItemBarColor();
                k = x + 2;
                l = y + 13;
                context.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                context.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
            }

            if(this.client != null)
            {
                ClientPlayerEntity clientPlayerEntity = this.client.player;

                float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());

                if(f > 0.0F)
                {
                    k = y + MathHelper.floor(16.0F * (1.0F - f));
                    l = k + MathHelper.ceil(16.0F * f);
                    context.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
                }
            }
            context.getMatrices().pop();
        }
    }

    public static List<ItemStack> getTools(Tiers.Tier tier, InventoryImproved inventory)
    {
        List<ItemStack> tools = new ArrayList<>();

        for(int i = tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST); i <= tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST) + (tier.getToolSlots() - 1); i++)
        {
            if(!inventory.getStack(i).isEmpty())
            {
                tools.add(inventory.getStack(i));
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}