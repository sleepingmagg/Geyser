/*
 * Copyright (c) 2019-2023 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.item.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.geyser.entity.type.living.animal.TropicalFishEntity;
import org.geysermc.geyser.item.TooltipOptions;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.text.MinecraftLocale;
import org.geysermc.geyser.translator.item.BedrockItemBuilder;
import org.geysermc.geyser.translator.text.MessageTranslator;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;

import java.util.List;

public class TropicalFishBucketItem extends Item {
    private static final Style LORE_STYLE = Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC);

    public TropicalFishBucketItem(String javaIdentifier, Builder builder) {
        super(javaIdentifier, builder);
    }

    @Override
    public void translateComponentsToBedrock(@NonNull GeyserSession session, @NonNull DataComponents components, @NonNull TooltipOptions tooltip, @NonNull BedrockItemBuilder builder) {
        super.translateComponentsToBedrock(session, components, tooltip, builder);

        // Prevent name from appearing as "Bucket of"
        builder.putByte("AppendCustomName", (byte) 1);
        builder.putString("CustomName", MinecraftLocale.getLocaleString("entity.minecraft.tropical_fish", session.locale()));

        // Add Java's client side lore tag
        Integer pattern = components.get(DataComponentTypes.TROPICAL_FISH_PATTERN);
        Integer baseColor = components.get(DataComponentTypes.TROPICAL_FISH_BASE_COLOR);
        Integer patternColor = components.get(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR);

        // The pattern component decides whether to show the tooltip of all 3 components, as of Java 1.21.5
        if ((pattern != null || (baseColor != null && patternColor != null)) && tooltip.showInTooltip(DataComponentTypes.TROPICAL_FISH_PATTERN)) {
            //TODO test this for 1.21.5
            int packedVariant = getPackedVariant(pattern, baseColor, patternColor);
            List<String> lore = builder.getOrCreateLore();

            int predefinedVariantId = TropicalFishEntity.getPredefinedId(packedVariant);
            if (predefinedVariantId != -1) {
                Component line = Component.translatable("entity.minecraft.tropical_fish.predefined." + predefinedVariantId, LORE_STYLE);
                lore.add(0, MessageTranslator.convertMessage(line, session.locale()));
            } else {
                Component typeTooltip = Component.translatable("entity.minecraft.tropical_fish.type." + TropicalFishEntity.getVariantName(packedVariant), LORE_STYLE);
                lore.add(0, MessageTranslator.convertMessage(typeTooltip, session.locale()));

                if (baseColor != null && patternColor != null) {
                    Component colorTooltip = Component.translatable("color.minecraft." + TropicalFishEntity.getColorName(baseColor.byteValue()), LORE_STYLE);
                    if (!baseColor.equals(patternColor)) {
                        colorTooltip = colorTooltip.append(Component.text(", ", LORE_STYLE))
                            .append(Component.translatable("color.minecraft." + TropicalFishEntity.getColorName(patternColor.byteValue()), LORE_STYLE));
                    }
                    lore.add(1, MessageTranslator.convertMessage(colorTooltip, session.locale()));
                }
            }
        }
    }

    private static int getPackedVariant(Integer pattern, Integer baseColor, Integer patternColor) {
        if (pattern == null) {
            pattern = 0;
        }
        if (baseColor == null) {
            baseColor = 0;
        }
        if (patternColor == null) {
            patternColor = 0;
        }
        return TropicalFishEntity.getPackedVariant(pattern, baseColor, patternColor);
    }
}
