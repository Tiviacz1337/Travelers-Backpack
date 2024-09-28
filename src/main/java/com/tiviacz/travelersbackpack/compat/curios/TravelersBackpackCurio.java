package com.tiviacz.travelersbackpack.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class TravelersBackpackCurio implements ICurio
{
    public final ItemStack stack;

    public TravelersBackpackCurio(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack)
    {
        if(!TravelersBackpackConfig.COMMON.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                CapabilityUtils.getCapability(player).ifPresent(data ->
                {
                    data.setWearable(stack);
                    data.setContents(stack);

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return false;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack)
    {
        if(!TravelersBackpackConfig.COMMON.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                CapabilityUtils.getCapability(player).ifPresent(data ->
                {
                    data.removeWearable();

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext)
    {
        if(!TravelersBackpackConfig.COMMON.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu || !CapabilityUtils.isWearingBackpack(player)) return;

            //Patch for Accessories dupe bug
            //if (TravelersBackpack.accessoriesLoaded) {
            //    if(AccessoriesPatch.isAccessoriesMenuOpened(player)) return;
            //} //#TODO check

            ItemStack backpack = CapabilityUtils.getWearingBackpack(player);

            if(!ItemStack.isSameItemSameTags(backpack, getStack()))
            {
                getStack().setTag(backpack.getTag());
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext)
    {
        return TravelersBackpackConfig.COMMON.backpackSettings.curiosIntegration.get();
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit)
    {
        return DropRule.DEFAULT;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCurioRenderer()
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> CuriosRendererRegistry.register(holder.get(), Renderer::new));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer
    {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float v, float v1, float v2, float v3, float v4, float v5)
        {
            if(slotContext.entity() instanceof Player player && renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {

                BackpackLayerModel<?> backpackLayerModel = BackpackLayerModel.LAYER_MODEL;
                backpackLayerModel.setBackpackStack(itemStack);

                TravelersBackpackLayer.renderBackpackLayer(backpackLayerModel, humanoidModel, poseStack, multiBufferSource, i, player, itemStack, v, v1, v2, v3, v4, v5);
            }
        }
    }
}