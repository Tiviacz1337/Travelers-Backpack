package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HoseSpecialRenderer() implements SelectItemModelProperty<Integer> {

    public static void register() {
        SelectItemModelProperties.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_modes"), HoseSpecialRenderer.TYPE);
    }

    public static final SelectItemModelProperty.Type<HoseSpecialRenderer, Integer> TYPE = SelectItemModelProperty.Type.create(
            // The map codec for this property
            MapCodec.unit(new HoseSpecialRenderer()),
            // The codec for the object being selected
            Codec.INT
    );

    @Override
    public @Nullable Integer get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.get(ModDataComponents.HOSE_MODES).get(0);
    }

    @Override
    public Codec<Integer> valueCodec() {
        return Codec.INT;
    }

    @Override
    public Type<? extends SelectItemModelProperty<Integer>, Integer> type() {
        return TYPE;
    }
}
