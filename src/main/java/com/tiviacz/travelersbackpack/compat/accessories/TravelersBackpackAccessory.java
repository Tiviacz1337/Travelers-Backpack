package com.tiviacz.travelersbackpack.compat.accessories;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class TravelersBackpackAccessory implements Accessory
{
    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference)
    {
        return TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if(reference.entity() instanceof PlayerEntity player)
        {
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if(!player.getWorld().isClient)
            {
                ComponentUtils.getComponent(player).setContents(stack);
                ComponentUtils.getComponent(player).setWearable(stack);
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if(reference.entity() instanceof PlayerEntity player)
        {
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) return;

            if(!player.getWorld().isClient)
            {
                ComponentUtils.getComponent(player).removeWearable();
            }
            ComponentUtils.sync(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration) return;

        if(reference.entity() instanceof PlayerEntity player)
        {
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler || !ComponentUtils.isWearingBackpack(player)) return;

            TravelersBackpackInventory container = ComponentUtils.getBackpackInv(player);

            if(!ItemStack.areItemsAndComponentsEqual(container.getItemStack(), stack))
            {
                stack.applyChanges(container.getItemStack().getComponentChanges());
                //this.onEquip(stack, slot, entity);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit()
    {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesRendererRegistry.registerRenderer(item, Renderer::new));
    }

    public static void init()
    {
        Registries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> AccessoriesAPI.registerAccessory(item, new TravelersBackpackAccessory()));
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer
    {
        public TravelersBackpackWearableModel model;

        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, MatrixStack matrices, EntityModel<M> entityModel, VertexConsumerProvider vertexConsumers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if(reference.entity() instanceof PlayerEntity player && entityModel instanceof BipedEntityModel bipedEntityModel)
            {
                TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
                if(inv == null) return;
                model = new TravelersBackpackWearableModel<>(player, vertexConsumers, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).createModel());
                boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

                //if(inv.getItemStack().isEmpty()) return;
                //Identifier id = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

                if(inv.getItemStack().isEmpty() || !(inv.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

                Identifier id = travelersBackpackItem.getBackpackTexture();

                boolean isColorable = false;
                boolean isCustomSleepingBag = false;

                if(inv.getItemStack().contains(DataComponentTypes.DYED_COLOR) && inv.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
                {
                    isColorable = true;
                    id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed.png");
                }

                if(inv.getItemStack().contains(ModComponentTypes.SLEEPING_BAG_COLOR))
                {
                    isCustomSleepingBag = true;
                }

                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(flag ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

                matrices.push();

                if(player.isSneaking())
                {
                    matrices.translate(0D, -0.155D, 0.025D);
                }

                bipedEntityModel.copyBipedStateTo(model);
                model.setupAngles(bipedEntityModel);

                matrices.translate(0, 0.175, 0.325);
                matrices.scale(0.85F, 0.85F, 0.85F);

                if(isColorable)
                {
                    model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fullAlpha(inv.getItemStack().get(DataComponentTypes.DYED_COLOR).rgb()));

                    id = Identifier.of(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
                }
                model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

                if(isCustomSleepingBag)
                {
                    id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
                }
                else
                {
                    id = ResourceUtils.getDefaultSleepingBagTexture();
                }

                vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
                model.sleepingBag.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);

                matrices.pop();
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, MatrixStack matrices) {

        }
    }
}