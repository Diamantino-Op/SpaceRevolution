package com.diamantino.spacerevolution.client;

import com.diamantino.spacerevolution.client.models.entities.AsteroidEntityModel;
import com.diamantino.spacerevolution.client.renderers.blockentities.CrusherBlockEntityRenderer;
import com.diamantino.spacerevolution.client.renderers.entities.AsteroidEntityRenderer;
import com.diamantino.spacerevolution.client.resourcepack.*;
import com.diamantino.spacerevolution.client.screen.CrusherScreen;
import com.diamantino.spacerevolution.initialization.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SpaceRevolutionClient implements ClientModInitializer {
    public static boolean hasUpdatedPlanets;
    public static List<SolarSystem> solarSystems = new ArrayList<>();
    public static List<PlanetSkyRenderer> skyRenderers = new ArrayList<>();
    public static List<PlanetRing> planetRings = new ArrayList<>();
    public static List<Galaxy> galaxies = new ArrayList<>();

    public static final EntityModelLayer asteroidLayer = new EntityModelLayer(new Identifier(ModReferences.modId, "asteroid"), "asteroid");

    @Override
    public void onInitializeClient() {
        //Networking
        ModMessages.registerS2CPackets();

        //Screens
        HandledScreens.register(ModScreenHandlers.crusherScreenHandler, CrusherScreen::new);

        //Blocks
        BlockEntityRendererFactories.register(ModBlockEntities.crusherBlockEntity, CrusherBlockEntityRenderer::new);

        for (Block block : ModBlocks.electricCables)
            BlockRenderLayerMapImpl.INSTANCE.putBlock(block, RenderLayer.getTranslucent());

        //Entities
        EntityModelLayerRegistry.registerModelLayer(asteroidLayer, AsteroidEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntityTypes.asteroidEntityType, AsteroidEntityRenderer::new);

        //Planets
        onRegisterReloadListeners((id, listener) -> ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
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

    public void onRegisterReloadListeners(BiConsumer<Identifier, SynchronousResourceReloader> registry) {
        registry.accept(new Identifier(ModReferences.modId, "planet_resources"), new PlanetResources());
    }
}
