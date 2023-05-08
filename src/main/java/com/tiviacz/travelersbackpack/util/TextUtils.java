package com.tiviacz.travelersbackpack.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtils
{
    public static List<Component> getTranslatedSplittedText(String translationId, @Nullable ChatFormatting style)
    {
        MutableComponent text = Component.translatable(translationId);

        if(text.getString().contains("\n"))
        {
            String[] translatedSplitted = I18n.get(translationId).split("\n");
            List<Component> list = new ArrayList<>();
            Arrays.stream(translatedSplitted).forEach(s -> list.add(style == null ? Component.literal(s) : Component.literal(s).withStyle(style)));
            return list;
        }
        return List.of(style == null ? text : text.withStyle(style));
    }
}