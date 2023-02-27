package com.diamantino.spacerevolution.client.resourcepack;

import com.diamantino.spacerevolution.data.ButtonColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public record Galaxy(Identifier galaxy, Identifier texture, ButtonColor buttonColor, int scale) {
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("galaxy").forGetter(Galaxy::galaxy),
            Identifier.CODEC.fieldOf("texture").forGetter(Galaxy::texture),
            ButtonColor.CODEC.fieldOf("button_color").forGetter(Galaxy::buttonColor),
            Codec.INT.fieldOf("scale").orElse(1).forGetter(Galaxy::scale)
    ).apply(instance, Galaxy::new));
}