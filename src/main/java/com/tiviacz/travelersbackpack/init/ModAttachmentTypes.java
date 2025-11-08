package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.TravelersBackpackAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

/**
 * Preparation for removal of the required Cardinal Components dependency.
 * This class is not used in version 1.21.x.
 * Use the component ComponentUtils#WEARABLE instead.
 * Currently, this class is only used for data transfer purposes.
 */
public class ModAttachmentTypes {
    public static final AttachmentType<TravelersBackpackAttachment> TRAVELERS_BACKPACK = AttachmentRegistry.create(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), builder -> builder
            .initializer(() -> TravelersBackpackAttachment.DEFAULT)
            .persistent(TravelersBackpackAttachment.CODEC));

    public static void init() {

    }
}