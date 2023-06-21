package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screen.widget.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.SAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.SEquipBackpackPacket;
import com.tiviacz.travelersbackpack.network.SSleepingBagPacket;
import com.tiviacz.travelersbackpack.network.SSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
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
    public static final ResourceLocation LEATHER_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/leather_travelers_backpack.png");
    public static final ResourceLocation IRON_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/iron_travelers_backpack.png");
    public static final ResourceLocation GOLD_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/gold_travelers_backpack.png");
    public static final ResourceLocation DIAMOND_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/diamond_travelers_backpack.png");
    public static final ResourceLocation NETHERITE_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/netherite_travelers_backpack.png");
    public static final ResourceLocation SETTINGS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    public static final ResourceLocation EXTRAS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_extras.png");
    private final ScreenImageButton BED_BUTTON_BORDER;
    private final ScreenImageButton BED_BUTTON;
    private final ScreenImageButton EQUIP_BUTTON;
    private final ScreenImageButton UNEQUIP_BUTTON;
    private final ScreenImageButton DISABLED_CRAFTING_BUTTON;
    private final ScreenImageButton ABILITY_SLIDER;
    public ControlTab controlTab;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;
    public TankSlotWidget leftTankSlotWidget;
    public TankSlotWidget rightTankSlotWidget;
    public CraftingWidget craftingWidget;

    public final ITravelersBackpackInventory inv;
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
        this.imageHeight = screenContainer.inventory.getTier().getImageHeight();

        this.BED_BUTTON_BORDER = new ScreenImageButton(5, 42 + screenContainer.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.BED_BUTTON = new ScreenImageButton(6, 43 + screenContainer.inventory.getTier().getMenuSlotPlacementFactor(), 16, 16);
        this.EQUIP_BUTTON = new ScreenImageButton(5, 42 + screenContainer.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.UNEQUIP_BUTTON = new ScreenImageButton(5, 42 + screenContainer.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 42 + screenContainer.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.ABILITY_SLIDER = new ScreenImageButton(5, screenContainer.inventory.getTier().getAbilitySliderRenderPos(), 18, 11);

        this.tankLeft = new TankScreen(inv.getLeftTank(), 25, 7, inv.getTier().getTankRenderPos(), 16);
        this.tankRight = new TankScreen(inv.getRightTank(), 207, 7, inv.getTier().getTankRenderPos(), 16);
    }

    @Override
    protected void init()
    {
        super.init();
        initControlTab();
        initSettingsTab();
        initTankSlotWidgets();
        initCraftingWidget();
    }

    public void initTankSlotWidgets()
    {
        if(this.inv.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget = new TankSlotWidget(this, leftPos, topPos, 28, 60);
            addWidget(leftTankSlotWidget);

            this.rightTankSlotWidget = new TankSlotWidget(this, leftPos + 220, topPos, 28, 60);
            addWidget(rightTankSlotWidget);
        }
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, leftPos + 61, topPos - 10, 50, 13);
        addWidget(controlTab);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, leftPos + imageWidth, topPos + 10, 15, 18);
        addWidget(settingsWidget);
        this.sortWidget = new SortWidget(this, leftPos + imageWidth, topPos + 29, 15, 18);
        addWidget(sortWidget);
        this.memoryWidget = new MemoryWidget(this, leftPos + imageWidth, topPos + 48, 15, 18);
        addWidget(memoryWidget);
    }

    public void initCraftingWidget()
    {
        this.craftingWidget = new CraftingWidget(this, leftPos + imageWidth, topPos + 29, 15, 18);
        addWidget(craftingWidget);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        this.minecraft.getTextureManager().bind(SETTINGS_TRAVELERS_BACKPACK);
        this.craftingWidget.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if(!this.inv.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(this, matrixStack);
        }
        if(!this.inv.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(this, matrixStack);
        }

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(EXTRAS_TRAVELERS_BACKPACK);

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(matrixStack, this, 76, 0);
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON_BORDER.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON_BORDER.draw(matrixStack, this, 19, 0);
                BED_BUTTON.draw(matrixStack, this, getBedIconX(inv.getSleepingBagColor()), getBedIconY(inv.getSleepingBagColor()));
            }
            else
            {
                BED_BUTTON_BORDER.draw(matrixStack, this, 0, 0);
                BED_BUTTON.draw(matrixStack, this, getBedIconX(inv.getSleepingBagColor()), getBedIconY(inv.getSleepingBagColor()));
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 114, 0);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 114, 12);
                    }
                }
                else
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 95, 0);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 95, 12);
                    }
                }
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 57, 0);
                }
                else
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 38, 0);
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
                            ABILITY_SLIDER.draw(matrixStack, this, 114, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 114, 12);
                        }
                    }
                    else
                    {
                        if(inv.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 95, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 95, 12);
                        }
                    }
                }

                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    UNEQUIP_BUTTON.draw(matrixStack, this, 57, 19);
                }
                else
                {
                    UNEQUIP_BUTTON.draw(matrixStack, this, 38, 19);
                }
            }
        }

        this.controlTab.render(matrixStack, mouseX, mouseY, partialTicks);

        if(this.inv.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget.render(matrixStack, mouseX, mouseY, partialTicks);
            this.rightTankSlotWidget.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        this.settingsWidget.render(matrixStack, mouseX, mouseY, partialTicks);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(matrixStack, mouseX, mouseY, partialTicks));

        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(this.screenID == Reference.TILE_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY) && !this.isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !this.isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                if(inv.getAbilityValue())
                {
                    List<IReorderingProcessor> list = new ArrayList<>();
                    list.add(new TranslationTextComponent("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, inv.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, inv.getItemStack()))
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

        if(TravelersBackpack.enableCurios() && !this.isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
        {
            if(CapabilityUtils.isWearingBackpack(getMenu().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
                }
            }

            if(!CapabilityUtils.isWearingBackpack(getMenu().playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.equip_integration"), mouseX, mouseY);
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting && !this.isWidgetVisible(Tiers.LEATHER, this.rightTankSlotWidget))
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }

        craftingWidget.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public boolean isWidgetVisible(Tiers.Tier tier, TankSlotWidget widget)
    {
        return this.inv.getTier().getOrdinal() == tier.getOrdinal() && widget.isVisible();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(getScreenTexture(inv.getTier()));
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(EXTRAS_TRAVELERS_BACKPACK);

        if(!inv.getSettingsManager().renderOverlay())
        {
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    blit(matrixStack, this.getGuiLeft() + 151 + (j * 18), this.getGuiTop() + (6 + this.inv.getTier().getMenuSlotPlacementFactor()) + i * 18, 213, 0, 18, 18);
                }
            }
        }

        if(!inv.getSlotManager().getUnsortableSlots().isEmpty() && !inv.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            inv.getSlotManager().getUnsortableSlots()
                    .forEach(i -> this.blit(matrixStack, this.getGuiLeft() + getX(i), this.getGuiTop() + getY(i), 77, 20, 16, 16));
        }

        if(!inv.getSlotManager().getMemorySlots().isEmpty())
        {
            this.setBlitOffset(100);
            this.itemRenderer.blitOffset = 100.0F;

            inv.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(inv.getSlotManager().isSelectorActive(SlotManager.MEMORY))
                        {
                            blit(matrixStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()), 115, 24, 16, 16);

                            if(!menu.getSlot(pair.getFirst() + 1).getItem().isEmpty())
                            {
                                drawMemoryOverlay(matrixStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                            }
                        }

                        if(!menu.getSlot(pair.getFirst() + 1).getItem().isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        RenderSystem.enableDepthTest();
                        this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                        drawMemoryOverlay(matrixStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                    });

            this.itemRenderer.blitOffset = 0.0F;
            this.setBlitOffset(0);
        }
    }

    public void drawMemoryOverlay(MatrixStack matrixStack, int x, int y)
    {
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(EXTRAS_TRAVELERS_BACKPACK);
        blit(matrixStack, x, y, 96, 24, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.popPose();
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType type)
    {
        super.slotClicked(slot, slotId, button, type);

        if((slotId >= 1 && slotId <= (inv.getTier().getStorageSlotsWithCrafting())) && inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            inv.getSlotManager().setUnsortableSlot(slotId - 1);
        }

        if((slotId >= 1 && slotId <= (inv.getTier().getStorageSlotsWithCrafting())) && inv.getSlotManager().isSelectorActive(SlotManager.MEMORY) && (!slot.getItem().isEmpty() || (slot.getItem().isEmpty() && inv.getSlotManager().isSlot(SlotManager.MEMORY, slotId - 1))))
        {
            inv.getSlotManager().setMemorySlot(slotId - 1, slot.getItem());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (inv.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(!inv.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(inv.getScreenID(), Reference.EMPTY_TANK, 1));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, inventory.player, inv.getLevel(), inv.getScreenID());
            }
        }

        if(!inv.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(inv.getScreenID(), Reference.EMPTY_TANK, 2));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, inventory.player, inv.getLevel(), inv.getScreenID());
            }
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON_BORDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new SSleepingBagPacket(inv.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new SAbilitySliderPacket(inv.getScreenID(), !inv.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }

        if(!inv.hasTileEntity())
        {
            if(!TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
                {
                    if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SEquipBackpackPacket(true));
                        return true;
                    }
                }

                if(CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
                {
                    if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SEquipBackpackPacket(false));
                        return true;
                    }
                }
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new SAbilitySliderPacket(inv.getScreenID(), !inv.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.25F, 1.0F);
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

    public ResourceLocation getScreenTexture(Tiers.Tier tier)
    {
        if(tier == Tiers.LEATHER) return LEATHER_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.IRON) return IRON_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.GOLD) return GOLD_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.DIAMOND) return DIAMOND_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.NETHERITE) return NETHERITE_SCREEN_TRAVELERS_BACKPACK;
        return LEATHER_SCREEN_TRAVELERS_BACKPACK;
    }

    public int getY(int slot)
    {
        if(this.inv.getTier() == Tiers.LEATHER)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 23) return 43;
        }

        if(this.inv.getTier() == Tiers.IRON)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 23) return 43;
            else if(slot <= 31) return 61;
        }

        if(this.inv.getTier() == Tiers.GOLD)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 23) return 43;
            else if(slot <= 31) return 61;
            else if(slot <= 39) return 79;
        }

        if(this.inv.getTier() == Tiers.DIAMOND)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 23) return 43;
            else if(slot <= 31) return 61;
            else if(slot <= 39) return 79;
            else if(slot <= 47) return 97;
        }

        if(this.inv.getTier() == Tiers.NETHERITE)
        {
            if(slot <= 8) return 7;
            else if(slot <= 17) return 25;
            else if(slot <= 26) return 43;
            else if(slot <= 35) return 61;
            else if(slot <= 44) return 79;
            else if(slot <= 52) return 97;
            else if(slot <= 60) return 115;
        }
        return 0;
    }

    public int getX(int slot)
    {
        if(this.inv.getTier() == Tiers.LEATHER)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 23)
            {
                return 62 + (18 * (slot - 16));
            }
        }

        if(this.inv.getTier() == Tiers.IRON)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 23)
            {
                return 62 + (18 * (slot - 16));
            }
            else if(slot >= 24 && slot <= 31)
            {
                return 62 + (18 * (slot - 24));
            }
        }

        if(this.inv.getTier() == Tiers.GOLD)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 23)
            {
                return 62 + (18 * (slot - 16));
            }
            else if(slot >= 24 && slot <= 31)
            {
                return 62 + (18 * (slot - 24));
            }
            else if(slot >= 32 && slot <= 39)
            {
                return 62 + (18 * (slot - 32));
            }
        }

        if(this.inv.getTier() == Tiers.DIAMOND)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * (slot));
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 23)
            {
                return 62 + (18 * (slot - 16));
            }
            else if(slot >= 24 && slot <= 31)
            {
                return 62 + (18 * (slot - 24));
            }
            else if(slot >= 32 && slot <= 39)
            {
                return 62 + (18 * (slot - 32));
            }
            else if(slot >= 40 && slot <= 47)
            {
                return 62 + (18 * (slot - 40));
            }
        }

        if(this.inv.getTier() == Tiers.NETHERITE)
        {
            if(slot >= 0 && slot <= 8)
            {
                return 44 + (18 * (slot));
            }
            else if(slot >= 9 && slot <= 17)
            {
                return 44 + (18 * (slot - 9));
            }
            else if(slot >= 18 && slot <= 26)
            {
                return 44 + (18 * (slot - 18));
            }
            else if(slot >= 27 && slot <= 35)
            {
                return 44 + (18 * (slot - 27));
            }
            else if(slot >= 36 && slot <= 44)
            {
                return 44 + (18 * (slot - 36));
            }
            else if(slot >= 45 && slot <= 52)
            {
                return 62 + (18 * (slot - 45));
            }
            else if(slot >= 53 && slot <= 60)
            {
                return 62 + (18 * (slot - 53));
            }
        }
        return 0;
    }

    public int getBedIconX(int colorId)
    {
        return 1 + (colorId <= 7 ? 0 : 19);
    }
    public int getBedIconY(int colorId)
    {
        return 19 + ((colorId % 8) * 17);
    }
}