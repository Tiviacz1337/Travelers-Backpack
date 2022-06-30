package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.network.ModNetwork;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TravelersBackpackHandledScreen extends HandledScreen<TravelersBackpackBaseScreenHandler>
{
    public static final Identifier SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton bedButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton equipButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton unequipButton = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton emptyTankButtonLeft = new ScreenImageButton(14, 86, 9, 9);
    private static final ScreenImageButton emptyTankButtonRight = new ScreenImageButton(225, 86, 9, 9);
    private static final ScreenImageButton disabledCraftingButton = new ScreenImageButton(225, 96, 18, 18);
    private final ITravelersBackpackInventory inventory;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackHandledScreen(TravelersBackpackBaseScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.inventory = handler.inventory;
        this.screenID = handler.inventory.getScreenID();

        this.x = 0;
        this.y = 0;

        this.backgroundWidth = 248;
        this.backgroundHeight = 207;

        this.tankLeft = new TankScreen(handler.inventory.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(handler.inventory.getRightTank(), 207, 7, 100, 16);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        if(!this.inventory.getLeftTank().isResourceBlank())
        {
            this.tankLeft.drawScreenFluidBar(matrices);
        }
        if(!this.inventory.getRightTank().isResourceBlank())
        {
            this.tankRight.drawScreenFluidBar(matrices);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderTooltip(matrices, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderTooltip(matrices, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(TravelersBackpackConfig.enableEmptyTankButton)
            {
                if(emptyTankButtonLeft.inButton(this, mouseX, mouseY) || emptyTankButtonRight.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(matrices, Text.translatable("screen.travelersbackpack.empty_tank"), mouseX, mouseY);
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(disabledCraftingButton.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrices, Text.translatable("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_TRAVELERS_BACKPACK);
        this.client.getTextureManager().bindTexture(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if(TravelersBackpackConfig.disableCrafting)
        {
            disabledCraftingButton.draw(matrices, this, 77, 208);
        }

        if(inventory.hasTileEntity())
        {
            if(bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(matrices, this, 20, 227);
            }
            else
            {
                bedButton.draw(matrices, this, 1, 227);
            }
        }
        else
        {
            if(!ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID && !TravelersBackpack.enableTrinkets())
            {
                if(equipButton.inButton(this, mouseX, mouseY))
                {
                    equipButton.draw(matrices, this, 58, 208);
                }
                else
                {
                    equipButton.draw(matrices,this, 39, 208);
                }
            }

            if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
            {
                if(!TravelersBackpack.enableTrinkets())
                {
                    if(unequipButton.inButton(this, mouseX, mouseY))
                    {
                        unequipButton.draw(matrices,this, 58, 227);
                    }
                    else
                    {
                        unequipButton.draw(matrices,this, 39, 227);
                    }
                }
            }

            if(TravelersBackpackConfig.enableEmptyTankButton)
            {
                if(emptyTankButtonLeft.inButton(this, mouseX, mouseY))
                {
                    emptyTankButtonLeft.draw(matrices,this, 29, 217);
                }
                else
                {
                    emptyTankButtonLeft.draw(matrices,this, 10, 217);
                }

                if(emptyTankButtonRight.inButton(this, mouseX, mouseY))
                {
                    emptyTankButtonRight.draw(matrices,this, 29, 217);
                }
                else
                {
                    emptyTankButtonRight.draw(matrices,this, 10, 217);
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
                ClientPlayNetworking.send(ModNetwork.DEPLOY_SLEEPING_BAG_ID, PacketByteBufs.create().writeBlockPos(inventory.getPosition()));
                return true;
            }
        }

        if(!inventory.hasTileEntity() && !ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID && !TravelersBackpack.enableTrinkets())
        {
            if(equipButton.inButton(this, (int)mouseX, (int)mouseY))
            {
                ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, PacketByteBufs.empty());
                return true;
            }
        }

        if(!inventory.hasTileEntity() && ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID && !TravelersBackpack.enableTrinkets())
        {
            if(unequipButton.inButton(this, (int)mouseX, (int)mouseY))
            {
                ClientPlayNetworking.send(ModNetwork.UNEQUIP_BACKPACK_ID, PacketByteBufs.empty());
                return true;
            }

            if(TravelersBackpackConfig.enableEmptyTankButton)
            {
                if(!inventory.getLeftTank().isResourceBlank())
                {
                    if(emptyTankButtonLeft.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        ClientPlayNetworking.send(ModNetwork.CYCLE_TOOL_ID,  PacketByteBufs.copy(PacketByteBufs.create().writeDouble(1.0D).writeByte(Reference.EMPTY_TANK)));
                    }
                }

                if(!inventory.getRightTank().isResourceBlank())
                {
                    if(emptyTankButtonRight.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        ClientPlayNetworking.send(ModNetwork.CYCLE_TOOL_ID,  PacketByteBufs.copy(PacketByteBufs.create().writeDouble(2.0D).writeByte(Reference.EMPTY_TANK)));
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

