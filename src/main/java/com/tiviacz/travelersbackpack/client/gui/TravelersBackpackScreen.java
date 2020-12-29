package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseContainer;
import com.tiviacz.travelersbackpack.network.CycleToolPacket;
import com.tiviacz.travelersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travelersbackpack.network.SleepingBagPacket;
import com.tiviacz.travelersbackpack.network.UnequipBackpackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends ContainerScreen<TravelersBackpackBaseContainer>
{
    public static final ResourceLocation SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton bedButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton equipButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton unequipButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton emptyTankButtonLeft = new ScreenImageButton(14, 86, 9, 9);
    private static final ScreenImageButton emptyTankButtonRight = new ScreenImageButton(225, 86, 9, 9);
    private static final ScreenImageButton disabledCraftingButton = new ScreenImageButton(225, 96, 18, 18);
    private final ITravelersBackpack inventory;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackScreen(TravelersBackpackBaseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.inventory = screenContainer.inventory;
        this.screenID = screenContainer.inventory.getScreenID();

        this.guiLeft = 0;
        this.guiTop = 0;

        this.xSize = 248;
        this.ySize = 207;

        this.tankLeft = new TankScreen(inventory.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(inventory.getRightTank(), 207, 7, 100, 16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(!this.inventory.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar();
        }
        if(!this.inventory.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.func_243308_b(matrixStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.func_243308_b(matrixStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(emptyTankButtonLeft.inButton(this, mouseX, mouseY) || emptyTankButtonRight.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.empty"), mouseX, mouseY);
            }
        }

        if(TravelersBackpackConfig.COMMON.disableCrafting.get())
        {
            if(disabledCraftingButton.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);

       if(TravelersBackpackConfig.COMMON.disableCrafting.get())
        {
            disabledCraftingButton.draw(matrixStack, this, 77, 208);
        }

        if(inventory.hasTileEntity())
        {
            if(bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(matrixStack, this, 20, 227);
            }
            else
            {
                bedButton.draw(matrixStack, this, 1, 227);
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID)
            {
                if(equipButton.inButton(this, mouseX, mouseY))
                {
                    equipButton.draw(matrixStack, this, 58, 208);
                }
                else
                {
                    equipButton.draw(matrixStack,this, 39, 208);
                }
            }

            if(CapabilityUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
            {
                if(unequipButton.inButton(this, mouseX, mouseY))
                {
                    unequipButton.draw(matrixStack,this, 58, 227);
                }
                else
                {
                    unequipButton.draw(matrixStack,this, 39, 227);
                }

                if(TravelersBackpackConfig.COMMON.enableEmptyTankButton.get())
                {
                    if(emptyTankButtonLeft.inButton(this, mouseX, mouseY))
                    {
                        emptyTankButtonLeft.draw(matrixStack,this, 29, 217);
                    }
                    else
                    {
                        emptyTankButtonLeft.draw(matrixStack,this, 10, 217);
                    }

                    if(emptyTankButtonRight.inButton(this, mouseX, mouseY))
                    {
                        emptyTankButtonRight.draw(matrixStack,this, 29, 217);
                    }
                    else
                    {
                        emptyTankButtonRight.draw(matrixStack,this, 10, 217);
                    }
                }
            }
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(inventory.hasTileEntity())
        {
            if(bedButton.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SleepingBagPacket(inventory.getPosition()));
                return true;
            }
        }

        if(!inventory.hasTileEntity() && !CapabilityUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID)
        {
            if(equipButton.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
                return true;
            }
        }

        if(!inventory.hasTileEntity() && CapabilityUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(unequipButton.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
                return true;
            }

            if(!inventory.getLeftTank().isEmpty())
            {
                if(emptyTankButtonLeft.inButton(this, (int)mouseX, (int)mouseY))
                {
                    TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1, Reference.EMPTY_TANK));
                }
            }

            if(!inventory.getRightTank().isEmpty())
            {
                if(emptyTankButtonRight.inButton(this, (int)mouseX, (int)mouseY))
                {
                    TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(2, Reference.EMPTY_TANK));
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

