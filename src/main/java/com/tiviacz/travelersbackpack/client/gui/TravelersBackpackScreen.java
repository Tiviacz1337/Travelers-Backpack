package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends ContainerScreen<TravelersBackpackBaseContainer>
{
    public static final ResourceLocation SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton BED_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton UNEQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 96, 18, 18);
    private static final ScreenImageButton ABILITY_SLIDER = new ScreenImageButton(5, 56,18, 11);
    private static final ScreenImageButton SORT_BUTTON = new ScreenImageButton(61, -10, 14, 13);
    private static final ScreenImageButton QUICK_STACK_BUTTON = new ScreenImageButton(75, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_BACKPACK_BUTTON = new ScreenImageButton(86, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_PLAYER_BUTTON = new ScreenImageButton(97, -10, 14, 13);
    private final ITravelersBackpackInventory inv;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackScreen(TravelersBackpackBaseContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn)
    {
        super(screenContainer, inventory, titleIn);
        this.inv = screenContainer.inventory;
        this.screenID = screenContainer.inventory.getScreenID();

        this.leftPos = 0;
        this.topPos = 0;

        this.imageWidth = 248;
        this.imageHeight = 207;

        this.tankLeft = new TankScreen(inv.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(inv.getRightTank(), 207, 7, 100, 16);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(!this.inv.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(matrixStack);
        }
        if(!this.inv.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(matrixStack);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(BackpackUtils.isShiftPressed())
        {
            if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.sort"), mouseX, mouseY);
            }

            if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
            {
                List<IReorderingProcessor> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack").getVisualOrderText());
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack_shift").getVisualOrderText());

                this.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
            {
                List<IReorderingProcessor> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack").getVisualOrderText());
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack_shift").getVisualOrderText());

                this.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }

        if(this.screenID == Reference.TILE_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY))
            {
                if(inv.getAbilityValue())
                {
                    List<IReorderingProcessor> list = new ArrayList<>();
                    list.add(new TranslationTextComponent("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, inv.getItemStack()))
                    {
                        list.add(inv.getLastTime() == 0 ? new TranslationTextComponent("screen.travelersbackpack.ability_ready").getVisualOrderText() : new StringTextComponent(BackpackUtils.getConvertedTime(inv.getLastTime())).getVisualOrderText());
                    }
                    this.renderTooltip(matrixStack, list, mouseX, mouseY);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                    }
                    else
                    {
                        this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                    }
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(matrixStack, this, 77, 208);
        }

        if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
        {
            SORT_BUTTON.draw(matrixStack, this, 134, 222);
        }
        else
        {
            SORT_BUTTON.draw(matrixStack, this, 134, 208);
        }

        if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
        {
            QUICK_STACK_BUTTON.draw(matrixStack, this, 148, 222);
        }
        else
        {
            QUICK_STACK_BUTTON.draw(matrixStack, this, 148, 208);
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrixStack, this, 159, 222);
        }
        else
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrixStack, this, 159, 208);
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrixStack, this, 170, 222);
        }
        else
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrixStack, this, 170, 208);
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON.draw(matrixStack, this, 20, 227);
            }
            else
            {
                BED_BUTTON.draw(matrixStack, this, 1, 227);
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 115, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 115, 220);
                    }
                }
                else
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 96, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 96, 220);
                    }
                }
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID && !TravelersBackpack.enableCurios())
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 58, 208);
                }
                else
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 39, 208);
                }
            }

            if(CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inv.getItemStack()))
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(inv.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 115, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 115, 220);
                        }
                    }
                    else
                    {
                        if(inv.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 96, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 96, 220);
                        }
                    }
                }

                if(!TravelersBackpack.enableCurios())
                {
                    if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                    {
                        UNEQUIP_BUTTON.draw(matrixStack, this, 58, 227);
                    }
                    else
                    {
                        UNEQUIP_BUTTON.draw(matrixStack, this, 39, 227);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(inv.getScreenID(), InventorySorter.SORT_BACKPACK, BackpackUtils.isShiftPressed(), inv.hasTileEntity() ? inv.getPosition() : null));
            inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(QUICK_STACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 76))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(inv.getScreenID(), InventorySorter.QUICK_STACK, BackpackUtils.isShiftPressed(), inv.hasTileEntity() ? inv.getPosition() : null));
            inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 87))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(inv.getScreenID(), InventorySorter.TRANSFER_TO_BACKPACK, BackpackUtils.isShiftPressed(), inv.hasTileEntity() ? inv.getPosition() : null));
            inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 98))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(inv.getScreenID(), InventorySorter.TRANSFER_TO_PLAYER, BackpackUtils.isShiftPressed(), inv.hasTileEntity() ? inv.getPosition() : null));
            inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(!inv.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(1, Reference.EMPTY_TANK, inv.getScreenID(), inv.getPosition()));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, inventory.player, inv.getLevel(), inv.getScreenID(), inv.getPosition());
            }
        }

        if(!inv.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(2, Reference.EMPTY_TANK, inv.getScreenID(), inv.getPosition()));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, inventory.player, inv.getLevel(), inv.getScreenID(), inv.getPosition());
            }
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SleepingBagPacket(inv.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!inv.getAbilityValue(), inv.getPosition()));
                inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
                return true;
            }
        }

        if(!inv.hasTileEntity() && !CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
        {
            if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
                return true;
            }
        }

        if(!inv.hasTileEntity() && CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!inv.getAbilityValue(), null));
                inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
                return true;
            }

            if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        if(ModClientEventHandler.OPEN_INVENTORY.isActiveAndMatches(InputMappings.getKey(p_keyPressed_1_, p_keyPressed_2_)))
        {
            ClientPlayerEntity playerEntity = this.getMinecraft().player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}