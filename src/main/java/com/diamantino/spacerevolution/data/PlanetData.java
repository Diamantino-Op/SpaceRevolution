package com.diamantino.spacerevolution.data;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.initialization.ModMessages;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import java.util.*;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class PlanetData extends JsonDataLoader {
    private static final Set<Planet> PLANETS = new HashSet<>();
    private static final Map<RegistryKey<World>, Planet> LEVEL_TO_PLANET = new HashMap<>();
    private static final Map<RegistryKey<World>, Planet> ORBIT_TO_PLANET = new HashMap<>();
    private static final Set<RegistryKey<World>> PLANET_LEVELS = new HashSet<>();
    private static final Set<RegistryKey<World>> ORBITS_LEVELS = new HashSet<>();
    private static final Set<RegistryKey<World>> OXYGEN_LEVELS = new HashSet<>();


    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public PlanetData() {
        super(GSON, "planet_data/planets");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> objects, ResourceManager resourceManager, Profiler profiler) {
        profiler.push("Space Revolution Planet Deserialization");
        List<Planet> planets = new ArrayList<>();

        for (Map.Entry<Identifier, JsonElement> entry : objects.entrySet()) {
            JsonObject jsonObject = JsonHelper.asObject(entry.getValue(), "planet");
            Planet newPlanet = Planet.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ModReferences.logger::error);
            planets.removeIf(planet -> planet.level().equals(newPlanet.level()));
            planets.add(newPlanet);
        }

        PlanetData.updatePlanets(planets);
        profiler.pop();
    }

    public static void updatePlanets(Collection<Planet> planets) {
        clear();
        for (Planet planet : new HashSet<>(planets)) {
            PLANETS.add(planet);
            LEVEL_TO_PLANET.put(planet.level(), planet);
            ORBIT_TO_PLANET.put(planet.orbitWorld(), planet);
            PLANET_LEVELS.add(planet.level());
            ORBITS_LEVELS.add(planet.orbitWorld());
            if (planet.hasOxygen()) {
                OXYGEN_LEVELS.add(planet.level());
            }
        }
    }

    private static void clear() {
        PLANETS.clear();
        LEVEL_TO_PLANET.clear();
        ORBIT_TO_PLANET.clear();
        PLANET_LEVELS.clear();
        ORBITS_LEVELS.clear();
        OXYGEN_LEVELS.clear();
    }

    public static void writePlanetData(PacketByteBuf buf) {
        PacketHelper.writeWithYabn(buf, Planet.CODEC.listOf(), PlanetData.planets().stream().toList(), true)
                .get()
                .mapRight(DataResult.PartialResult::message)
                .ifRight(ModReferences.logger::error);
    }

    public static void readPlanetData(PacketByteBuf buf) {
        PacketHelper.readWithYabn(buf, Planet.CODEC.listOf(), true)
                .get()
                .ifLeft(PlanetData::updatePlanets)
                .mapRight(DataResult.PartialResult::message)
                .ifRight(ModReferences.logger::error);
    }

    public static Set<Planet> planets() {
        return PLANETS;
    }

    public static Optional<Planet> getPlanetFromLevel(RegistryKey<World> level) {
        return Optional.ofNullable(LEVEL_TO_PLANET.get(level));
    }

    public static Optional<Planet> getPlanetFromOrbit(RegistryKey<World> level) {
        return Optional.ofNullable(ORBIT_TO_PLANET.get(level));
    }

    public static boolean isOrbitLevel(RegistryKey<World> level) {
        return ORBITS_LEVELS.contains(level);
    }

    public static boolean isPlanetLevel(World level) {
        if (level.isClient() && !SpaceRevolutionClient.hasUpdatedPlanets) {
            ClientPlayNetworking.send(ModMessages.requestPlanetDataPacket, PacketByteBufs.create());
            SpaceRevolutionClient.hasUpdatedPlanets = true;
        }
        return PLANET_LEVELS.contains(level.getRegistryKey());
    }

    public static boolean isOxygenated(RegistryKey<World> level) {
        return OXYGEN_LEVELS.contains(level);
    }
}