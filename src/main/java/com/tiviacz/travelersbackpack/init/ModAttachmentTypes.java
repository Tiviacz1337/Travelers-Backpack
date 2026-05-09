package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.BackpackAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TravelersBackpack.MODID);

    public static final Supplier<AttachmentType<BackpackAttachment>> TRAVELERS_BACKPACK = ATTACHMENT_TYPES.register("travelers_backpack",
            () -> AttachmentType.builder(BackpackAttachment::new).serialize(new BackpackAttachment.Serializer()).copyOnDeath().build());
}