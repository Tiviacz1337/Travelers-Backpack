package com.tiviacz.travelersbackpack.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;
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
    public void onEquipFromUse(SlotContext slotContext)
    {
        if(!TravelersBackpackConfig.COMMON.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
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

            TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

            if(!ItemStack.isSameItemSameTags(container.getItemStack(), getStack()))
            {
                getStack().setTag(container.getItemStack().getOrCreateTag());
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
        public TravelersBackpackWearableModel model;

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float v, float v1, float v2, float v3, float v4, float v5)
        {
            if(slotContext.entity() instanceof Player player && renderLayerParent.getModel() instanceof HumanoidModel humanoidModel)
            {
                TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);
                if(container == null) return;

                model = new TravelersBackpackWearableModel(player, multiBufferSource, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
                boolean flag = container.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

                ResourceLocation loc = ResourceUtils.getBackpackTexture(container.getItemStack().getItem());

                boolean isColorable = false;
                boolean isCustomSleepingBag = false;

                if(container.getItemStack().getTag() != null && container.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                {
                    if(BackpackDyeRecipe.hasColor(container.getItemStack()))
                    {
                        isColorable = true;
                        loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
                    }
                }

                if(container.getItemStack().getTag() != null)
                {
                    if(container.getItemStack().getTag().contains(ITravelersBackpackContainer.SLEEPING_BAG_COLOR))
                    {
                        isCustomSleepingBag = true;
                    }
                }

                VertexConsumer vertexConsumer = multiBufferSource.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

                poseStack.pushPose();

                if(player.isCrouching())
                {
                    poseStack.translate(0D, -0.155D, 0.025D);
                }

                humanoidModel.copyPropertiesTo(model);
                model.setupAngles(humanoidModel);

                poseStack.translate(0, 0.175, 0.325);
                poseStack.scale(0.85F, 0.85F, 0.85F);

                if(isColorable)
                {
                    Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(container.getItemStack()));
                    model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

                    loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                    vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutout(loc));
                }

                model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                if(isCustomSleepingBag)
                {
                    loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
                }
                else
                {
                    loc = ResourceUtils.getDefaultSleepingBagTexture();
                }

                vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutout(loc));
                model.sleepingBag.render(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                poseStack.popPose();
            }
        }
    }
}