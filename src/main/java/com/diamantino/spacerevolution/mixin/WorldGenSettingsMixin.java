package com.diamantino.spacerevolution.mixin;

import com.diamantino.spacerevolution.world.LevelSeed;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldGenSettings.class)
public class WorldGenSettingsMixin {
    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/gen/GeneratorOptions;Lnet/minecraft/world/dimension/DimensionOptionsRegistryHolder;)V")
    private void generatorOptions(GeneratorOptions worldOptions, DimensionOptionsRegistryHolder worldDimensions, CallbackInfo ci) {
        LevelSeed.setSeed(worldOptions.getSeed());
    }
}
