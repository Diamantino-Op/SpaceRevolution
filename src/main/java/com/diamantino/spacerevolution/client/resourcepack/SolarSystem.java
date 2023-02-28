package com.diamantino.spacerevolution.client.resourcepack;

import com.diamantino.spacerevolution.data.ButtonColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public record SolarSystem(Identifier galaxy, Identifier solarSystem, Identifier sun, int sunScale, ButtonColor buttonColor, Color ringColour) {
    public static final Codec<SolarSystem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("galaxy").forGetter(SolarSystem::galaxy),
            Identifier.CODEC.fieldOf("solar_system").forGetter(SolarSystem::solarSystem),
            Identifier.CODEC.fieldOf("sun").forGetter(SolarSystem::sun),
            Codec.INT.fieldOf("sun_scale").forGetter(SolarSystem::sunScale),
            ButtonColor.CODEC.fieldOf("button_color").forGetter(SolarSystem::buttonColor),
            Color.CODEC.fieldOf("ring_color").forGetter(SolarSystem::ringColour)
    ).apply(instance, SolarSystem::new));

}