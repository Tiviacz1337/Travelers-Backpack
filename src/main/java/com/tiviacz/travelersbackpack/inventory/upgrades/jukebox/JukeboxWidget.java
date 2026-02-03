package com.tiviacz.travelersbackpack.inventory.upgrades.jukebox;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class JukeboxWidget extends UpgradeWidgetBase<JukeboxUpgrade> {
    private final WidgetElement playButton = new WidgetElement(new Point(24, 22), new Point(18, 18));
    private final WidgetElement stopButton = new WidgetElement(new Point(42, 22), new Point(18, 18));

    public JukeboxWidget(BackpackScreen screen, JukeboxUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 103), "screen.travelersbackpack.jukebox_upgrade");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if(isTabOpened()) {
            if(isMouseOverPlayButton(mouseX, mouseY)) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + playButton.pos().x(), pos.y() + playButton.pos().y(), 24, 18, playButton.size().x(), playButton.size().y());
            }
            if(isMouseOverStopButton(mouseX, mouseY)) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + stopButton.pos().x(), pos.y() + stopButton.pos().y(), 24, 18, stopButton.size().x(), stopButton.size().y());
            }
        }
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);

        if(isTabOpened()) {
            if(this.upgrade.isPlayingRecord()) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 6, pos.y() + 22, 24, 36, 18, 18);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.upgrade.getUpgradeManager().getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
            if(isMouseOverPlayButton(pMouseX, pMouseY) && isBackpackOwner()) {
                if(isTabOpened() && this.upgrade.canPlayRecord()) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, this.dataHolderSlot, true, ServerActions.PLAY_RECORD);
                    playDiscToPlayer(screen.getMenu().getPlayerInventory().player.getId(), getFromDisk(upgrade.diskHandler.getStackInSlot(0)), getDescription(upgrade.diskHandler.getStackInSlot(0)));
                    this.screen.playUIClickSound();
                    return true;
                }
            }
        }

        if(isMouseOverStopButton(pMouseX, pMouseY) && isBackpackOwner()) {
            if(isTabOpened() && this.upgrade.isPlayingRecord()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, this.dataHolderSlot, false, ServerActions.PLAY_RECORD);
                if(this.upgrade.getUpgradeManager().getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                    stopDisc(getFromDisk(upgrade.diskHandler.getStackInSlot(0)));
                }
                this.screen.playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean isMouseOverPlayButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.playButton);
    }

    public boolean isMouseOverStopButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.stopButton);
    }

    @Nullable
    public SoundEvent getFromDisk(ItemStack stack) {
        if(stack.getItem() instanceof RecordItem record) {
            return record.getSound();
        }
        return null;
    }

    @Nullable
    public Component getDescription(ItemStack stack) {
        if(stack.getItem() instanceof RecordItem record) {
            return record.getDisplayName();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void playDiscToPlayer(int entityId, @Nullable SoundEvent jukeboxSong, @Nullable Component description) {
        if(jukeboxSong == null) {
            return;
        }

        var level = Minecraft.getInstance().level;
        if(level == null) {
            return;
        }

        var entity = level.getEntity(entityId);
        if(entity == null) {
            return;
        }

        Minecraft.getInstance().getSoundManager().queueTickingSound(new MovingSound(entity, jukeboxSong));

        if(description != null) {
            Minecraft.getInstance().gui.setNowPlaying(description);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void stopDisc(SoundEvent jukeboxSong) {
        if(jukeboxSong == null) {
            return;
        }
        Minecraft.getInstance().getSoundManager().stop(jukeboxSong.getLocation(), SoundSource.NEUTRAL);
    }

    public static class MovingSound extends AbstractTickableSoundInstance {

        private final Entity entity;

        public MovingSound(Entity entityIn, SoundEvent soundIn) {
            super(soundIn, SoundSource.NEUTRAL, entityIn.level().getRandom());
            this.entity = entityIn;
            this.looping = false;
            this.delay = 0;
            this.volume = 1.0F;
        }

        @Override
        public void tick() {
            if(this.entity instanceof Player player) {
                if(!CapabilityUtils.isWearingBackpack(player) || !shouldStopPlaying(player)) {
                    this.stop();
                }
            }
            if(!this.entity.isAlive()) {
                this.stop();
            } else {
                this.x = (float)this.entity.getX();
                this.y = (float)this.entity.getY();
                this.z = (float)this.entity.getZ();
            }
        }

        public boolean shouldStopPlaying(Player player) {
            return CapabilityUtils.getBackpackWrapper(player, CapabilityUtils.UPGRADES_ONLY.get()).getUpgradeManager().getUpgrade(JukeboxUpgrade.class).isPresent();
        }
    }
}
