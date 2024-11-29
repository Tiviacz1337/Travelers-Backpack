package com.tiviacz.travelersbackpackold.client.screen;


import com.tiviacz.travelersbackpackold.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TankScreen
{
    private final int height;
    private final int width;
    private final int startX;
    private final int startY;
    private final SingleVariantStorage<FluidVariant> tank;

    public TankScreen(SingleVariantStorage<FluidVariant> tank, int x, int y, int height, int width)
    {
        this.startX = x;
        this.startY = y;
        this.height = height;
        this.width = width;
        this.tank = tank;
    }

    public List<Text> getTankTooltip(World world)
    {
        FluidVariant fluidVariant = tank.getResource();
        List<Text> tankTips = new ArrayList<>();
        String fluidName = !fluidVariant.isBlank() ? FluidVariantAttributes.getName(fluidVariant).getString() : I18n.translate("screen.travelersbackpack.none");
        String fluidAmount = I18n.translate("screen.travelersbackpack.empty");

        if(!fluidVariant.isBlank())
        {
            float amount = (float)tank.getAmount() / 81;
            float capacity = (float)tank.getCapacity() / 81;

            fluidAmount = (int)amount + "/" + (int)capacity;
        }

        if(!fluidVariant.isBlank())
        {
            if(fluidVariant.getComponents().entrySet().stream().anyMatch(entry -> entry.getKey().equals(DataComponentTypes.POTION_CONTENTS)))
            {
                fluidName = null;
                PotionContentsComponent contents = fluidVariant.getComponents().get(DataComponentTypes.POTION_CONTENTS).get();
                contents.buildTooltip(tankTips::add, 1.0F, world.getTickManager().getTickRate());
            }
        }

        if(fluidName != null) tankTips.add(Text.literal(fluidName));
        tankTips.add(Text.literal(fluidAmount));

        return tankTips;
    }

    public void drawScreenFluidBar(TravelersBackpackHandledScreen screen, DrawContext context)
    {
        RenderUtils.renderScreenTank(context, tank, screen.getX() + this.startX, screen.getY() + this.startY, 0, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackHandledScreen screen, int mouseX, int mouseY)
    {
        return screen.getX() + startX <= mouseX && mouseX <= startX + width + screen.getX() && startY + screen.getY() <= mouseY && mouseY <= startY + height + screen.getY();
    }
}