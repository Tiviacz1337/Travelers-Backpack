package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.widget.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelersBackpackHandledScreen extends HandledScreen<TravelersBackpackBaseScreenHandler>
{
    public static final Identifier LEATHER_SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/leather_travelers_backpack.png");
    public static final Identifier IRON_SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/iron_travelers_backpack.png");
    public static final Identifier GOLD_SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/gold_travelers_backpack.png");
    public static final Identifier DIAMOND_SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/diamond_travelers_backpack.png");
    public static final Identifier NETHERITE_SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/netherite_travelers_backpack.png");
    public static final Identifier SETTINGS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    public static final Identifier EXTRAS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_extras.png");
    private final ScreenImageButton BED_BUTTON_BORDER;
    private final ScreenImageButton BED_BUTTON;
    private final ScreenImageButton EQUIP_BUTTON;
    private final ScreenImageButton UNEQUIP_BUTTON;
    private final ScreenImageButton ABILITY_SLIDER;
    public ControlTab controlTab;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;
    public TankSlotWidget leftTankSlotWidget;
    public TankSlotWidget rightTankSlotWidget;
    public CraftingWidget craftingWidget;

    public final ITravelersBackpackInventory inventory;
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
        this.backgroundHeight = handler.inventory.getTier().getImageHeight();

        this.BED_BUTTON_BORDER = new ScreenImageButton(5, 42 + handler.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.BED_BUTTON = new ScreenImageButton(6, 43 + handler.inventory.getTier().getMenuSlotPlacementFactor(), 16, 16);
        this.EQUIP_BUTTON = new ScreenImageButton(5, 42 + handler.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.UNEQUIP_BUTTON = new ScreenImageButton(5, 42 + handler.inventory.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.ABILITY_SLIDER = new ScreenImageButton(5, handler.inventory.getTier().getAbilitySliderRenderPos(), 18, 11);

        this.tankLeft = new TankScreen(handler.inventory.getLeftTank(), 25, 7, handler.inventory.getTier().getTankRenderPos(), 16);
        this.tankRight = new TankScreen(handler.inventory.getRightTank(), 207, 7, handler.inventory.getTier().getTankRenderPos(), 16);
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
    protected void init()
    {
        super.init();
        initControlTab();
        initToolSlotsWidget();
        initSettingsTab();
        initTankSlotWidgets();
        initCraftingWidget();
    }

    public void initTankSlotWidgets()
    {
        if(this.inventory.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget = new TankSlotWidget(this, x, y, 28, 60);
            addChild(leftTankSlotWidget);

            this.rightTankSlotWidget = new TankSlotWidget(this, x + 220, y, 28, 60);
            addChild(rightTankSlotWidget);
        }
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, x + 61, y - 10, 50, 13);
        addChild(controlTab);
    }

    public void initToolSlotsWidget()
    {
        this.toolSlotsWidget = new ToolSlotsWidget(this, x + 5, y - 15, 18, 15);
        addChild(toolSlotsWidget);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, x + backgroundWidth, y + 10, 15, 18);
        addChild(settingsWidget);
        this.sortWidget = new SortWidget(this, x + backgroundWidth, y + 29, 15, 18);
        addChild(sortWidget);
        this.memoryWidget = new MemoryWidget(this, x + backgroundWidth, y + 48, 15, 18);
        addChild(memoryWidget);
    }

    public void initCraftingWidget()
    {
        this.craftingWidget = new CraftingWidget(this, x + backgroundWidth, y + 29, 15, 18);
        addChild(craftingWidget);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) { }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);

        this.client.getTextureManager().bindTexture(SETTINGS_TRAVELERS_BACKPACK);
        this.craftingWidget.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);

        if(!this.inventory.getLeftTank().isResourceBlank())
        {
            this.tankLeft.drawScreenFluidBar(this, matrices);
        }
        if(!this.inventory.getRightTank().isResourceBlank())
        {
            this.tankRight.drawScreenFluidBar(this, matrices);
        }

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(EXTRAS_TRAVELERS_BACKPACK);

        if(inventory.hasTileEntity())
        {
            if(BED_BUTTON_BORDER.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON_BORDER.draw(matrices, this, 19, 0);
                BED_BUTTON.draw(matrices, this, getBedIconX(inventory.getSleepingBagColor()), getBedIconY(inventory.getSleepingBagColor()));
            }
            else
            {
                BED_BUTTON_BORDER.draw(matrices, this, 0, 0);
                BED_BUTTON.draw(matrices, this, getBedIconX(inventory.getSleepingBagColor()), getBedIconY(inventory.getSleepingBagColor()));
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()))
            {
                if(!inventory.getSettingsManager().showToolSlots())
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(inventory.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrices, this, 114, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrices, this, 114, 12);
                        }
                    }
                    else
                    {
                        if(inventory.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrices, this, 95, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrices, this, 95, 12);
                        }
                    }
                }
            }
        }
        else
        {
            if(!ComponentUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(matrices, this, 57, 0);
                }
                else
                {
                    EQUIP_BUTTON.draw(matrices,this, 38, 0);
                }
            }

            if(ComponentUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 57, 19);
                }
                else
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 38, 19);
                }

                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inventory.getItemStack()))
                {
                    if(!inventory.getSettingsManager().showToolSlots())
                    {
                        if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                        {
                            if(inventory.getAbilityValue())
                            {
                                ABILITY_SLIDER.draw(matrices, this, 114, 0);
                            }
                            else
                            {
                                ABILITY_SLIDER.draw(matrices, this, 114, 12);
                            }
                        }
                        else
                        {
                            if(inventory.getAbilityValue())
                            {
                                ABILITY_SLIDER.draw(matrices, this, 95, 0);
                            }
                            else
                            {
                                ABILITY_SLIDER.draw(matrices, this, 95, 12);
                            }
                        }
                    }
                }
            }
        }

        this.controlTab.render(matrices, mouseX, mouseY, delta);
        this.toolSlotsWidget.render(matrices, mouseX, mouseY, delta);

        if(this.inventory.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget.render(matrices, mouseX, mouseY, delta);
            this.rightTankSlotWidget.render(matrices, mouseX, mouseY, delta);
        }

        this.settingsWidget.render(matrices, mouseX, mouseY, delta);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(matrices, mouseX, mouseY, delta));

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y)
    {
        super.drawMouseoverTooltip(matrices, x, y);

        if(this.tankLeft.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankLeft.getTankTooltip(), x, y);
        }

        if(this.tankRight.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankRight.getTankTooltip(), x, y);
        }

        if(this.screenID == Reference.BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, x, y)
                    && !this.isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !this.isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                if(!inventory.getSettingsManager().showToolSlots())
                {
                    if(inventory.getAbilityValue())
                    {
                        List<Text> list = new ArrayList<>();
                        list.add(new TranslatableText("screen.travelersbackpack.ability_enabled"));
                        if(Arrays.stream(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST).anyMatch(s -> s.asItem() == inventory.getItemStack().getItem() || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, inventory.getItemStack())))
                        {
                            list.add(inventory.getLastTime() == 0 ? new TranslatableText("screen.travelersbackpack.ability_ready") : new LiteralText(BackpackUtils.getConvertedTime(inventory.getLastTime())));
                        }
                        this.renderTooltip(matrices, list, x, y);
                    }
                    else
                    {
                        if(!TravelersBackpackConfig.enableBackpackAbilities || !BackpackAbilities.ALLOWED_ABILITIES.contains(inventory.getItemStack().getItem()))
                        {
                            this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled_config"), x, y);
                        }
                        else
                        {
                            this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled"), x, y);
                        }
                    }
                }
            }
        }

        craftingWidget.drawMouseoverTooltip(matrices, x, y);
    }

    public boolean isWidgetVisible(Tiers.Tier tier, TankSlotWidget widget)
    {
        return this.inventory.getTier().getOrdinal() == tier.getOrdinal() && widget.isVisible();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(getScreenTexture(inventory.getTier()));
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(EXTRAS_TRAVELERS_BACKPACK);

        if(!inventory.getSettingsManager().renderOverlay() || TravelersBackpackConfig.disableCrafting)
        {
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    this.drawTexture(matrices, this.getX() + 151 + (j * 18), this.getY() + (6 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18, 213, 0, 18, 18);
                }
            }
        }

        if(inventory.getSettingsManager().showToolSlots())
        {
            for(int i = 0; i < inventory.getTier().getToolSlots(); i++)
            {
                boolean disabled = false;
                if(this.handler.getSlot(inventory.getTier().getStorageSlotsWithCrafting() + i + 1) instanceof ToolSlot)
                {
                    ToolSlot toolSlot = (ToolSlot)this.handler.getSlot(inventory.getTier().getStorageSlotsWithCrafting() + i + 1);
                    disabled = !toolSlot.canAccessPlace() || !toolSlot.canAccessPickup();
                }
                drawTexture(matrices, this.getX() + 5, this.getY() + 6 + (18 * i), 232, disabled ? 38 : 0, 18, 18);
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            drawTexture(matrices, this.getX() + 205, this.getY() + this.inventory.getTier().getMenuSlotPlacementFactor() + 42, 213, 19, 38, 18);
        }

        if(!inventory.getSlotManager().getUnsortableSlots().isEmpty() && !inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            inventory.getSlotManager().getUnsortableSlots()
                    .forEach(i -> drawTexture(matrices, this.getX() + handler.getSlot(i + 1).x, this.getY() + handler.getSlot(i + 1).y, 77, 20, 16, 16));
        }

        if(!inventory.getSlotManager().getMemorySlots().isEmpty())
        {
            this.setZOffset(100);
            this.itemRenderer.zOffset = 100.0F;

            inventory.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
                        {
                            drawTexture(matrices, this.x + handler.getSlot(pair.getFirst() + 1).x, this.y + handler.getSlot(pair.getFirst() + 1).y, 115, 24, 16, 16);

                            if(!handler.getSlot(pair.getFirst() + 1).getStack().isEmpty())
                            {
                                drawMemoryOverlay(matrices, this.x + handler.getSlot(pair.getFirst() + 1).x, this.y + handler.getSlot(pair.getFirst() + 1).y);
                            }
                        }

                        if(!handler.getSlot(pair.getFirst() + 1).getStack().isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        RenderSystem.enableDepthTest();
                        this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemstack, this.x + handler.getSlot(pair.getFirst() + 1).x, this.y + handler.getSlot(pair.getFirst() + 1).y);
                        drawMemoryOverlay(matrices, this.getX() + handler.getSlot(pair.getFirst() + 1).x, this.getY() + handler.getSlot(pair.getFirst() + 1).y);
                    });

            this.itemRenderer.zOffset = 0.0F;
            this.setZOffset(0);
        }
    }

    public void drawMemoryOverlay(MatrixStack matrices, int x, int y)
    {
        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(EXTRAS_TRAVELERS_BACKPACK);
        drawTexture(matrices, x, y, 96, 24, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);

        if((slotId >= 1 && slotId <= (inventory.getTier().getStorageSlotsWithCrafting())) && inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            inventory.getSlotManager().setUnsortableSlot(slotId - 1);
        }

        if((slotId >= 1 && slotId <= (inventory.getTier().getStorageSlotsWithCrafting())) && inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY) && (!slot.getStack().isEmpty() || (slot.getStack().isEmpty() && inventory.getSlotManager().isSlot(SlotManager.MEMORY, slotId - 1))))
        {
            inventory.getSlotManager().setMemorySlot(slotId - 1, slot.getStack());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(!inventory.getLeftTank().isResourceBlank())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(screenID).writeByte(Reference.EMPTY_TANK).writeDouble(1.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        if(!inventory.getRightTank().isResourceBlank())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(screenID).writeByte(Reference.EMPTY_TANK).writeDouble(2.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        if(inventory.hasTileEntity())
        {
            if(BED_BUTTON_BORDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
            {
                ClientPlayNetworking.send(ModNetwork.DEPLOY_SLEEPING_BAG_ID, PacketByteBufs.create().writeBlockPos(inventory.getPosition()));
                return true;
            }

            if(!inventory.getSettingsManager().showToolSlots())
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screenID).writeBoolean(!inventory.getAbilityValue());

                    ClientPlayNetworking.send(ModNetwork.ABILITY_SLIDER_ID, buf);
                    playUIClickSound();
                    return true;
                }
            }
        }

        if(!inventory.hasTileEntity())
        {
            if(!ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
            {
                if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(true);

                    ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                    return true;
                }
            }

            if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(false);

                    ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                    return true;
                }

                if(!inventory.getSettingsManager().showToolSlots())
                {
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
                    {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeByte(screenID).writeBoolean(!inventory.getAbilityValue());

                        ClientPlayNetworking.send(ModNetwork.ABILITY_SLIDER_ID, buf);
                        playUIClickSound();
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        playerInventory.player.world.playSound(playerInventory.player, playerInventory.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(KeybindHandler.OPEN_INVENTORY.matchesKey(keyCode, scanCode))
        {
            ClientPlayerEntity playerEntity = this.client.player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Identifier getScreenTexture(Tiers.Tier tier)
    {
        if(tier == Tiers.LEATHER) return LEATHER_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.IRON) return IRON_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.GOLD) return GOLD_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.DIAMOND) return DIAMOND_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.NETHERITE) return NETHERITE_SCREEN_TRAVELERS_BACKPACK;
        return LEATHER_SCREEN_TRAVELERS_BACKPACK;
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