package com.diamantino.spacerevolution.client.resourcepack;

import com.diamantino.spacerevolution.client.SpaceRevolutionClient;
import com.diamantino.spacerevolution.client.dimension.ModSkies;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// CREDIT: https://github.com/terrarium-earth/Ad-Astra
@Environment(EnvType.CLIENT)
public class PlanetResources implements IdentifiableResourceReloadListener {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        List<PlanetSkyRenderer> skyRenderers = new ArrayList<>();
        List<SolarSystem> solarSystems = new ArrayList<>();
        List<PlanetRing> planetRings = new ArrayList<>();
        List<Galaxy> galaxies = new ArrayList<>();

        // Sky Renderers
        for (Identifier id : manager.findAllResources("planet_resources/sky_renderers", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                for (Resource resource : manager.getAllResources(id)) {
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                    if (jsonObject != null) {
                        skyRenderers.add(PlanetSkyRenderer.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ModReferences.logger::error));
                    }
                }
            } catch (Exception e) {
                ModReferences.logger.error("Failed to load Space Revolution sky rendering assets from: \"" + id.toString() + "\"", e);
                e.printStackTrace();
            }
        }

        // Solar Systems
        for (Identifier id : manager.findAllResources("planet_resources/solar_systems", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                for (Resource resource : manager.getAllResources(id)) {
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                    if (jsonObject != null) {
                        solarSystems.add(SolarSystem.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ModReferences.logger::error));
                    }
                }
            } catch (Exception e) {
                ModReferences.logger.error("Failed to load Space Revolution solar system assets from: \"" + id.toString() + "\"", e);
                e.printStackTrace();
            }
        }

        for (Identifier id : manager.findAllResources("planet_resources/planet_rings", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                for (Resource resource : manager.getAllResources(id)) {
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                    if (jsonObject != null) {
                        planetRings.add(PlanetRing.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ModReferences.logger::error));
                    }
                }
            } catch (Exception e) {
                ModReferences.logger.error("Failed to load Space Revolution planet ring assets from: \"" + id.toString() + "\"", e);
                e.printStackTrace();
            }
        }

        for (Identifier id : manager.findAllResources("planet_resources/galaxy", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                for (Resource resource : manager.getAllResources(id)) {
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                    if (jsonObject != null) {
                        galaxies.add(Galaxy.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ModReferences.logger::error));
                    }
                }
            } catch (Exception e) {
                ModReferences.logger.error("Failed to load Space Revolution galaxy assets from: \"" + id.toString() + "\"", e);
                e.printStackTrace();
            }
        }

        solarSystems.sort(Comparator.comparing(SolarSystem::solarSystem));
        galaxies.sort(Comparator.comparing(Galaxy::galaxy));
        SpaceRevolutionClient.skyRenderers = skyRenderers;
        SpaceRevolutionClient.solarSystems = solarSystems;
        SpaceRevolutionClient.planetRings = planetRings;
        SpaceRevolutionClient.galaxies = galaxies;
        ModSkies.register();

        return null;
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(ModReferences.modId, "planet_resources");
    }
}