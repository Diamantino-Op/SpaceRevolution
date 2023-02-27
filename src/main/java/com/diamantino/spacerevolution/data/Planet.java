package com.diamantino.spacerevolution.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public record Planet(String translation, Identifier galaxy, Identifier solarSystem, RegistryKey<World> level, RegistryKey<World> orbitWorld, RegistryKey<World> parentWorld, int rocketTier, float gravity, boolean hasAtmosphere, int daysInYear, float temperature, long solarPower, long orbitSolarPower, boolean hasOxygen, ButtonColor buttonColor) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation").forGetter(Planet::translation),
            Identifier.CODEC.fieldOf("galaxy").forGetter(Planet::galaxy),
            Identifier.CODEC.fieldOf("solar_system").forGetter(Planet::solarSystem),
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("world").forGetter(Planet::level),
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("orbit_world").forGetter(Planet::orbitWorld),
            RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("parent_world").forGetter(Planet::getParentlevel),
            Codec.INT.fieldOf("rocket_tier").forGetter(Planet::rocketTier),
            Codec.FLOAT.fieldOf("gravity").forGetter(Planet::gravity),
            Codec.BOOL.fieldOf("has_atmosphere").forGetter(Planet::hasAtmosphere),
            Codec.INT.fieldOf("days_in_year").forGetter(Planet::daysInYear),
            Codec.FLOAT.fieldOf("temperature").forGetter(Planet::temperature),
            Codec.LONG.fieldOf("solar_power").forGetter(Planet::solarPower),
            Codec.LONG.fieldOf("orbit_solar_power").forGetter(Planet::orbitSolarPower),
            Codec.BOOL.fieldOf("has_oxygen").forGetter(Planet::hasOxygen),
            ButtonColor.CODEC.fieldOf("button_color").forGetter(Planet::buttonColor)
    ).apply(instance, Planet::new));

    public Planet(String translation, Identifier galaxy, Identifier solarSystem, RegistryKey<World> level, RegistryKey<World> orbitWorld, Optional<RegistryKey<World>> parentWorld, int rocketTier, float gravity, boolean hasAtmosphere, int daysInYear, float temperature, long solarPower, long orbitSolarPower, boolean hasOxygen, ButtonColor buttonColor) {
        this(translation, galaxy, solarSystem, level, orbitWorld, parentWorld.orElse(null), rocketTier, gravity, hasAtmosphere, daysInYear, temperature, solarPower, orbitSolarPower, hasOxygen, buttonColor);
    }

    private Optional<RegistryKey<World>> getParentlevel() {
        return Optional.ofNullable(parentWorld);
    }
}