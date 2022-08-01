package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends AbstractContainerScreen<TravelersBackpackBaseMenu> implements MenuAccess<TravelersBackpackBaseMenu>
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

        if(BackpackUtils.isShiftPressed())
        {
            if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
            {
                this.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.sort"), mouseX, mouseY);
            }

            if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
            {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(new TranslatableComponent("screen.travelersbackpack.quick_stack").getVisualOrderText());
                list.add(new TranslatableComponent("screen.travelersbackpack.quick_stack_shift").getVisualOrderText());

                this.renderTooltip(poseStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
            {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(new TranslatableComponent("screen.travelersbackpack.transfer_to_backpack").getVisualOrderText());
                list.add(new TranslatableComponent("screen.travelersbackpack.transfer_to_backpack_shift").getVisualOrderText());

                this.renderTooltip(poseStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
            {
                this.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }

        if(this.screenID == Reference.BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY))
            {
                if(container.getAbilityValue())
                {
                    List<FormattedCharSequence> list = new ArrayList<>();
                    list.add(new TranslatableComponent("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, container.getItemStack()))
                    {
                        list.add(container.getLastTime() == 0 ? new TranslatableComponent("screen.travelersbackpack.ability_ready").getVisualOrderText() : new TextComponent(BackpackUtils.getConvertedTime(container.getLastTime())).getVisualOrderText());
                    }
                    this.renderTooltip(poseStack, list, mouseX, mouseY);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                    }
                    else
                    {
                        this.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                    }
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
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

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(poseStack, this, 77, 208);
        }

        if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
        {
            SORT_BUTTON.draw(poseStack, this, 134, 222);
        }
        else
        {
            SORT_BUTTON.draw(poseStack, this, 134, 208);
        }

        if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
        {
            QUICK_STACK_BUTTON.draw(poseStack, this, 148, 222);
        }
        else
        {
            QUICK_STACK_BUTTON.draw(poseStack, this, 148, 208);
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(poseStack, this, 159, 222);
        }
        else
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(poseStack, this, 159, 208);
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(poseStack, this, 170, 222);
        }
        else
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(poseStack, this, 170, 208);
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
           if(!CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.ITEM_SCREEN_ID && !TravelersBackpack.enableCurios())
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

           if(CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
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
           }
       }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(container.getScreenID(), ContainerSorter.SORT_BACKPACK, BackpackUtils.isShiftPressed(), container.hasBlockEntity() ? container.getPosition() : null));
            menu.player.level.playSound(menu.player, menu.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(QUICK_STACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 76))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(container.getScreenID(), ContainerSorter.QUICK_STACK, BackpackUtils.isShiftPressed(), container.hasBlockEntity() ? container.getPosition() : null));
            menu.player.level.playSound(menu.player, menu.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 87))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(container.getScreenID(), ContainerSorter.TRANSFER_TO_BACKPACK, BackpackUtils.isShiftPressed(), container.hasBlockEntity() ? container.getPosition() : null));
            menu.player.level.playSound(menu.player, menu.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 98))
        {
            TravelersBackpack.NETWORK.sendToServer(new SorterPacket(container.getScreenID(), ContainerSorter.TRANSFER_TO_PLAYER, BackpackUtils.isShiftPressed(), container.hasBlockEntity() ? container.getPosition() : null));
            menu.player.level.playSound(menu.player, menu.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            return true;
        }

        if(!container.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(1, Reference.EMPTY_TANK, container.getScreenID(), container.getPosition()));

                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, menu.player, container.getLevel(), container.getScreenID(), container.getPosition());
            }
        }

        if(!container.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(2, Reference.EMPTY_TANK, container.getScreenID(), container.getPosition()));

                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, menu.player, container.getLevel(), container.getScreenID(), container.getPosition());
            }
        }

        if(container.hasBlockEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SleepingBagPacket(container.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!container.getAbilityValue(), container.getPosition()));
                menu.inventory.player.level.playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
                return true;
            }
        }

        if(!container.hasBlockEntity())
        {
            if(!TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.ITEM_SCREEN_ID)
                {
                    if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
                        return true;
                    }
                }

                if(CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
                        return true;
                    }
                }
            }

            if(CapabilityUtils.isWearingBackpack(getMenu().player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
                {
                    TravelersBackpack.NETWORK.sendToServer(new AbilitySliderPacket(!container.getAbilityValue(), null));
                    menu.inventory.player.level.playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        if(ModClientEventHandler.OPEN_INVENTORY.isActiveAndMatches(InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_)))
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