package com.tiviacz.travelersbackpack.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtils
{
    public static List<ITextComponent> getTranslatedSplittedText(String translationId, @Nullable TextFormatting style)
    {
        IFormattableTextComponent text = new TranslationTextComponent(translationId);

        if(text.getString().contains("\n"))
        {
            String[] translatedSplitted = I18n.get(translationId).split("\n");
            List<ITextComponent> list = new ArrayList<>();
            Arrays.stream(translatedSplitted).forEach(s -> list.add(style == null ? new StringTextComponent(s) : new StringTextComponent(s).withStyle(style)));
            return list;
        }
        return Arrays.asList(style == null ? text : text.withStyle(style));
    }
}