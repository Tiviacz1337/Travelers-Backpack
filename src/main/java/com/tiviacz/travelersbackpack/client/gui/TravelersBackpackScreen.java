package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventsHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends AbstractContainerScreen<TravelersBackpackBaseMenu> implements MenuAccess<TravelersBackpackBaseMenu>
{
    public static final ResourceLocation SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton BED_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton UNEQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EMPTY_TANK_LEFT_BUTTON = new ScreenImageButton(14, 86, 9, 9);
    private static final ScreenImageButton EMPTY_TANK_RIGHT_BUTTON = new ScreenImageButton(225, 86, 9, 9);
    private static final ScreenImageButton DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 96, 18, 18);
    private static final ScreenImageButton ABILITY_SLIDER = new ScreenImageButton(5, 56,18, 11);
    private final ITravelersBackpackContainer container;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackScreen(TravelersBackpackBaseMenu screenContainer, Inventory inventory, Component component)
    {
        super(screenContainer, inventory, component);
        this.container = screenContainer.container;
        this.screenID = screenContainer.container.getScreenID();

        this.leftPos = 0;
        this.topPos = 0;

        this.imageWidth = 248;
        this.imageHeight = 207;

        this.tankLeft = new TankScreen(container.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(container.getRightTank(), 207, 7, 100, 16);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        if(!this.container.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(poseStack);
        }
        if(!this.container.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(poseStack);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(poseStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(poseStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(TravelersBackpackConfig.COMMON.enableEmptyTankButton.get())
            {
                if(EMPTY_TANK_LEFT_BUTTON.inButton(this, mouseX, mouseY) || EMPTY_TANK_RIGHT_BUTTON.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.empty_tank"), mouseX, mouseY);
                }
            }
        }

        if(this.screenID == Reference.TRAVELERS_BACKPACK_BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY))
            {
                if(container.getAbilityValue())
                {
                    List<FormattedCharSequence> list = new ArrayList<>();
                    list.add(Component.translatable("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, container.getItemStack()))
                    {
                        list.add(container.getLastTime() == 0 ? Component.translatable("screen.travelersbackpack.ability_ready").getVisualOrderText() : Component.translatable(BackpackUtils.getConvertedTime(container.getLastTime())).getVisualOrderText());
                    }
                    this.renderTooltip(poseStack, list, mouseX, mouseY);
                }
                else
                {
                    this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                }
            }
        }

        if(TravelersBackpackConfig.SERVER.disableCrafting.get())
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

       if(TravelersBackpackConfig.SERVER.disableCrafting.get())
        {
            DISABLED_CRAFTING_BUTTON.draw(poseStack, this, 77, 208);
        }

        if(container.hasBlockEntity())
        {
            if(BED_BUTTON.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON.draw(poseStack, this, 20, 227);
            }
            else
            {
                BED_BUTTON.draw(poseStack, this, 1, 227);
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(container.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 115, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 115, 220);
                    }
                }
                else
                {
                    if(container.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 96, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 96, 220);
                    }
                }
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID && !TravelersBackpack.enableCurios())
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(poseStack, this, 58, 208);
                }
                else
                {
                    EQUIP_BUTTON.draw(poseStack,this, 39, 208);
                }
            }

            if(CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, container.getItemStack()))
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(container.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 115, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 115, 220);
                        }
                    }
                    else
                    {
                        if(container.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 96, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 96, 220);
                        }
                    }
                }

                if(!TravelersBackpack.enableCurios())
                {
                    if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                    {
                        UNEQUIP_BUTTON.draw(poseStack,this, 58, 227);
                    }
                    else
                    {
                        UNEQUIP_BUTTON.draw(poseStack,this, 39, 227);
                    }
                }

                if(TravelersBackpackConfig.COMMON.enableEmptyTankButton.get())
                {
                    if(EMPTY_TANK_LEFT_BUTTON.inButton(this, mouseX, mouseY))
                    {
                        EMPTY_TANK_LEFT_BUTTON.draw(poseStack,this, 29, 217);
                    }
                    else
                    {
                        EMPTY_TANK_LEFT_BUTTON.draw(poseStack,this, 10, 217);
                    }

                    if(EMPTY_TANK_RIGHT_BUTTON.inButton(this, mouseX, mouseY))
                    {
                        EMPTY_TANK_RIGHT_BUTTON.draw(poseStack,this, 29, 217);
                    }
                    else
                    {
                        EMPTY_TANK_RIGHT_BUTTON.draw(poseStack,this, 10, 217);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(container.hasBlockEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SleepingBagPacket(container.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!container.getAbilityValue(), true, container.getPosition()));
                menu.inventory.player.level.playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
                return true;
            }
        }

        if(!container.hasBlockEntity() && !CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID)
        {
            if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
                return true;
            }
        }

        if(!container.hasBlockEntity() && CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!container.getAbilityValue(), false, null));
                container.setAbility(!container.getAbilityValue());
                menu.inventory.player.level.playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
                return true;
            }

            if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
                return true;
            }

            if(TravelersBackpackConfig.COMMON.enableEmptyTankButton.get())
            {
                if(!container.getLeftTank().isEmpty())
                {
                    if(EMPTY_TANK_LEFT_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(1, Reference.EMPTY_TANK));
                    }
                }

                if(!container.getRightTank().isEmpty())
                {
                    if(EMPTY_TANK_RIGHT_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(2, Reference.EMPTY_TANK));
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        if(ModClientEventsHandler.OPEN_INVENTORY.isActiveAndMatches(InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_)))
        {
            LocalPlayer playerEntity = this.getMinecraft().player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}