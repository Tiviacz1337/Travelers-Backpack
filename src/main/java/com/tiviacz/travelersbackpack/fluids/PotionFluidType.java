package com.tiviacz.travelersbackpack.fluids;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PotionFluidType extends FluidType
{
    public static final ResourceLocation POTION_STILL_RL = new ResourceLocation(TravelersBackpack.MODID, "block/potion_still");
    public static final ResourceLocation POTION_FLOW_RL = new ResourceLocation(TravelersBackpack.MODID, "block/potion_flow");

    public PotionFluidType(Properties properties)
    {
        super(properties);
    }

    @Override
    public Component getDescription(FluidStack stack)
    {
        return Component.translatable(this.getDescriptionId(stack));
    }

    @Override
    public String getDescriptionId(FluidStack stack)
    {
        return PotionUtils.getPotion(stack.getTag()).getName("item.minecraft.potion.effect.");
    }

    @Override
    public String getDescriptionId()
    {
        return "item.minecraft.potion.effect.empty";
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions() {

            private static final int EMPTY_COLOR = 0xf800f8;

            @Override
            public int getTintColor()
            {
                return EMPTY_COLOR | 0xFF000000;
            }

            @Override
            public int getTintColor(FluidStack stack)
            {
                return getTintColor(stack.getTag()) | 0xFF000000;
            }

            private static int getTintColor(@Nullable CompoundTag tag)
            {
                if(tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC))
                {
                    return tag.getInt("CustomPotionColor");
                }

                if(PotionUtils.getPotion(tag) == Potions.EMPTY)
                {
                    return EMPTY_COLOR;
                }

                return PotionUtils.getColor(PotionUtils.getAllEffects(tag));
            }

            @Override
            public ResourceLocation getStillTexture() {
                return POTION_STILL_RL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return POTION_FLOW_RL;
            }
        });
    }
}