package com.diamantino.spacerevolution.data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import java.util.HashSet;
import java.util.Set;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
public class PlanetData extends IdentifiableResourceReloadListener {

    private static final Set<Planet> PLANETS = new HashSet<>();
    private static final Map<ResourceKey<Level>, Planet> LEVEL_TO_PLANET = new HashMap<>();
    private static final Map<ResourceKey<Level>, Planet> ORBIT_TO_PLANET = new HashMap<>();
    private static final Set<ResourceKey<Level>> PLANET_LEVELS = new HashSet<>();
    private static final Set<ResourceKey<Level>> ORBITS_LEVELS = new HashSet<>();
    private static final Set<ResourceKey<Level>> OXYGEN_LEVELS = new HashSet<>();


    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public PlanetData() {
        super(GSON, "planet_data/planets");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("Ad Astra Planet Deserialization");
        List<Planet> planets = new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "planet");
            Planet newPlanet = Planet.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, AdAstra.LOGGER::error);
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

    public static void writePlanetData(FriendlyByteBuf buf) {
        PacketHelper.writeWithYabn(buf, Planet.CODEC.listOf(), PlanetData.planets().stream().toList(), true)
                .get()
                .mapRight(DataResult.PartialResult::message)
                .ifRight(AdAstra.LOGGER::error);
    }

    public static void readPlanetData(FriendlyByteBuf buf) {
        PacketHelper.readWithYabn(buf, Planet.CODEC.listOf(), true)
                .get()
                .ifLeft(PlanetData::updatePlanets)
                .mapRight(DataResult.PartialResult::message)
                .ifRight(AdAstra.LOGGER::error);
    }

    public static Set<Planet> planets() {
        return PLANETS;
    }

    public static Optional<Planet> getPlanetFromLevel(ResourceKey<Level> level) {
        return Optional.ofNullable(LEVEL_TO_PLANET.get(level));
    }

    public static Optional<Planet> getPlanetFromOrbit(ResourceKey<Level> level) {
        return Optional.ofNullable(ORBIT_TO_PLANET.get(level));
    }

    public static boolean isOrbitLevel(ResourceKey<Level> level) {
        return ORBITS_LEVELS.contains(level);
    }

    public static boolean isPlanetLevel(Level level) {
        if (level.isClientSide && !AdAstraClient.hasUpdatedPlanets) {
            NetworkHandling.CHANNEL.sendToServer(new RequestPlanetDataPacket());
            AdAstraClient.hasUpdatedPlanets = true;
        }
        return PLANET_LEVELS.contains(level.dimension());
    }

    public static boolean isOxygenated(ResourceKey<Level> level) {
        return OXYGEN_LEVELS.contains(level);
    }
}