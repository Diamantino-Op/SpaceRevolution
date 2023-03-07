package com.diamantino.spacerevolution.initialization;

import net.minecraft.entity.damage.DamageSource;

public class ModDamageSources {
    public static DamageSource electricityDamageSource = new DamageSource("electricity").setBypassesArmor();
    public static DamageSource acidRainDamageSource = new DamageSource("acid_rain").setBypassesArmor();
    public static DamageSource freezingRainDamageSource = new DamageSource("freezing_rain");
    public static DamageSource oxygenDamageSource = new DamageSource("oxygen").setBypassesArmor().setBypassesProtection();
    public static DamageSource antiAsteroidDamageSource = new DamageSource("anti_asteroid").setBypassesArmor().setBypassesProtection();
    public static DamageSource asteroidImpactDamageSource = new DamageSource("asteroid_impact");

    public static void registerModEntityTypes() {
        ModReferences.logger.debug("Registering ModEntityTypes for " + ModReferences.modId);
    }
}
