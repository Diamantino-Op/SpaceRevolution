package com.diamantino.spacerevolution;

import com.diamantino.spacerevolution.config.ModCommonConfigs;
import com.diamantino.spacerevolution.data.PlanetData;
import com.diamantino.spacerevolution.initialization.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class SpaceRevolution implements ModInitializer {
    @Override
    public void onInitialize() {
        ModReferences.registerModReferences();
        ModCommonConfigs.initModConfigs();
        ModItemGroups.registerItemGroups();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
        ModRecipes.registerRecipes();
        ModMessages.registerC2SPackets();
        ModFeatures.registerModFeatures();
        ModParticleTypes.registerModParticleTypes();
        ModEntityTypes.registerModEntityTypes();
        ModDamageSources.registerModEntityTypes();
        //TODO: Remove snow from planets that don't have precipitations

        onRegisterReloadListeners((id, listener) -> ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return listener.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
            }

            @Override
            public Identifier getFabricId() {
                return id;
            }
        }));
    }

    public void onRegisterReloadListeners(BiConsumer<Identifier, ResourceReloader> registry) {
        registry.accept(new Identifier(ModReferences.modId, "planet_data"), new PlanetData());
    }
}
