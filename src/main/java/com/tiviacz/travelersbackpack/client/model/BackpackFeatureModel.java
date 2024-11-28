package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.client.renderer.FluidPart;
import com.tiviacz.travelersbackpack.client.renderer.StackPart;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BackpackFeatureModel<T extends LivingEntity> extends BipedEntityModel<T> {
    public static final BackpackFeatureModel<?> FEATURE_MODEL = new BackpackFeatureModel<>(BackpackModelData.createTravelersBackpack(true).createModel());

    public ModelPart mainBody;
    public ModelPart tankLeftTop;
    public ModelPart tankRightTop;
    public ModelPart sleepingBag;
    public ModelPart sleepingBagExtras;

    public ModelPart villagerNose;
    public ModelPart wolfNose;
    public ModelPart foxNose;
    public ModelPart ocelotNose;
    public ModelPart pigNose;

    public StackPart stacks;
    public FluidPart fluids;

    @Nullable
    private ItemStack backpackStack;
    @Nullable
    private LivingEntity livingEntity;
    @Nullable
    private VertexConsumerProvider vertices;

    public BackpackFeatureModel(ModelPart rootPart) {
        super(rootPart);

        this.mainBody = rootPart.getChild("body").getChild("main_body");
        this.tankLeftTop = rootPart.getChild("body").getChild("tankLeftTop");
        this.tankRightTop = rootPart.getChild("body").getChild("tankRightTop");
        this.sleepingBag = rootPart.getChild("body").getChild("sleepingBag");
        this.sleepingBagExtras = rootPart.getChild("body").getChild("sleepingBagExtras");

        //Noses, Additions

        this.villagerNose = rootPart.getChild("body").getChild("villagerNose");
        this.ocelotNose = rootPart.getChild("body").getChild("ocelotNose");
        this.pigNose = rootPart.getChild("body").getChild("pigNose");
        this.foxNose = rootPart.getChild("body").getChild("foxNose");
        this.wolfNose = rootPart.getChild("body").getChild("wolfNose");

        this.stacks = new StackPart(rootPart.getChild("body").getChild("stacks"));
        this.fluids = new FluidPart(rootPart.getChild("body").getChild("fluids"));
    }

    public void setLivingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public void setVertexConsumerProvider(VertexConsumerProvider vertices) {
        this.vertices = vertices;
    }

    public void setBackpackStack(ItemStack stack) {
        this.backpackStack = stack;
    }

    public ItemStack getBackpackStack() {
        if(this.backpackStack != null && this.backpackStack.getItem() instanceof TravelersBackpackItem) {
            return this.backpackStack;
        } else {
            if (this.livingEntity instanceof PlayerEntity playerEntity) {
                return ComponentUtils.getWearingBackpack(playerEntity);
            } else {
                return this.livingEntity.getEquippedStack(EquipmentSlot.CHEST);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
    {
        this.sleepingBag.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.sleepingBagExtras.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tankLeftTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tankRightTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.mainBody.render(matrices, vertices, light, overlay, red, green, blue, alpha);

        if(this.livingEntity != null) {
            //Item item = this.livingEntity instanceof PlayerEntity ? ComponentUtils.getWearingBackpack((PlayerEntity)this.livingEntity).getItem() : this.livingEntity.getEquippedStack(EquipmentSlot.CHEST).getItem();
            Item item = getBackpackStack().getItem();

            if(item == ModItems.FOX_TRAVELERS_BACKPACK) {
                this.foxNose.render(matrices, vertices, light, overlay);
            }

            if(item == ModItems.WOLF_TRAVELERS_BACKPACK) {
                this.wolfNose.render(matrices, vertices, light, overlay);
            }

            if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK) {
                this.villagerNose.render(matrices, vertices, light, overlay);
            }

            if(item == ModItems.OCELOT_TRAVELERS_BACKPACK) {
                this.ocelotNose.render(matrices, vertices, light, overlay);
            }

            if(item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK) {
                this.pigNose.render(matrices, vertices, light, overlay);
            }
        }

        if (this.livingEntity instanceof PlayerEntity player && this.vertices != null) {
            if (TravelersBackpackConfig.getConfig().client.renderTools) {
                this.stacks.prepare(getBackpackStack(), player, this.vertices);
                this.stacks.render(matrices, vertices, light, overlay);
            }
            this.fluids.prepare(getBackpackStack(), this.vertices);
            this.fluids.render(matrices, vertices, light, overlay);
        }
    }

    public void setupAngles(BipedEntityModel<T> model)
    {
        //Backpack
        this.mainBody.copyTransform(model.body);
        this.sleepingBag.copyTransform(model.body);
        this.sleepingBagExtras.copyTransform(model.body);
        this.tankLeftTop.copyTransform(model.body);
        this.tankRightTop.copyTransform(model.body);

        //Noses
        this.villagerNose.copyTransform(model.body);
        this.pigNose.copyTransform(model.body);
        this.ocelotNose.copyTransform(model.body);
        this.wolfNose.copyTransform(model.body);
        this.foxNose.copyTransform(model.body);

        if(this.livingEntity instanceof PlayerEntity)
        {
            //Extras
            this.stacks.copyTransform(model.body);
            this.fluids.copyTransform(model.body);
        }
    } //#TODO check alpha == 0.25F

   /* @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
    {
        if(alpha == 0.25F)
        {
            this.sleepingBag.render(matrices, vertices, light, overlay, red, green, blue, 1.0F);
        }
        else
        {
            this.sleepingBag.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.sleepingBagExtras.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankLeftTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.tankRightTop.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.mainBody.render(matrices, vertices, light, overlay, red, green, blue, alpha);

            if(this.livingEntity != null)
            {
                Item item = this.livingEntity instanceof PlayerEntity ? ComponentUtils.getWearingBackpack((PlayerEntity)this.livingEntity).getItem() : ComponentUtils.getWearingBackpack(this.livingEntity).getItem();

                if(item == ModItems.FOX_TRAVELERS_BACKPACK)
                {
                    this.foxNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.WOLF_TRAVELERS_BACKPACK)
                {
                    this.wolfNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK)
                {
                    this.villagerNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.OCELOT_TRAVELERS_BACKPACK)
                {
                    this.ocelotNose.render(matrices, vertices, light, overlay);
                }

                if(item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK)
                {
                    this.pigNose.render(matrices, vertices, light, overlay);
                }
            }

            c
        }
    } */

   /* public void setupAngles(BipedEntityModel<T> model)
    {
        //Backpack
        this.mainBody.copyTransform(model.body);
        this.sleepingBag.copyTransform(model.body);
        this.sleepingBagExtras.copyTransform(model.body);
        this.tankLeftTop.copyTransform(model.body);
        this.tankRightTop.copyTransform(model.body);

        //Noses
        this.villagerNose.copyTransform(model.body);
        this.pigNose.copyTransform(model.body);
        this.ocelotNose.copyTransform(model.body);
        this.wolfNose.copyTransform(model.body);
        this.foxNose.copyTransform(model.body);

        if(this.livingEntity instanceof PlayerEntity)
        {
            //Extras
            this.stacks.copyTransform(model.body);
            this.fluids.copyTransform(model.body);
        }
    } */
}