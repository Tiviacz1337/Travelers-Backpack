package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.TravelersBackpackAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModAttachmentTypes {
    public static final AttachmentType<TravelersBackpackAttachment> TRAVELERS_BACKPACK = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), builder -> builder
            .initializer(() -> new TravelersBackpackAttachment(new ItemStack(Items.AIR, 0)))
            .persistent(TravelersBackpackAttachment.CODEC));

    public static void init() {

    }
}