package com.diamantino.spacerevolution.config;

import com.diamantino.spacerevolution.initialization.ModReferences;

import java.nio.file.Path;

public class ModCommonConfigs {
    private static final Config config = new Config(Path.of("config/spacerevolution/common.json"));

    //Config values
    public static ConfigValue.FloatValue orbitLevelGravity = new ConfigValue.FloatValue("orbit_level_gravity", "The gravity of the various orbit dimensions.", 0.0f);
    public static ConfigValue.IntValue atmosphereLeaveLevel = new ConfigValue.IntValue("atmosphere_leave_level", "The height at which you leave the atmosphere and enter space.", 500);
    public static ConfigValue.IntValue minAsteroidSpawnTime = new ConfigValue.IntValue("min_asteroid_spawn_time", "The minimum delay (in ticks) after an asteroid will spawn.", 12000);
    public static ConfigValue.IntValue maxAsteroidSpawnTime = new ConfigValue.IntValue("max_asteroid_spawn_time", "The maximum delay (in ticks) after an asteroid will spawn.", 36000);
    public static ConfigValue.DoubleValue explosionSpeedThreshold = new ConfigValue.DoubleValue("explosion_speed_threshold", "The velocity to trigger a vehicle explosion while falling.", -1.2);
    public static ConfigValue.FloatValue vehicleExplosionMultiplier = new ConfigValue.FloatValue("vehicle_explosion_multiplier", "How much the explosion should be multiplied by when the vehicle has fallen.", 0.7f);
    public static ConfigValue.DoubleValue vehicleGravity = new ConfigValue.DoubleValue("vehicle_gravity", "The gravity of vehicles.", -2.0);

    public static void initModConfigs() {
        ModReferences.logger.debug("Initializing ModConfigs for " + ModReferences.modId);

        if (config.configFileExists()) {
            config.loadConfigFile();

            config.getFloatValue(orbitLevelGravity);
            config.getIntValue(atmosphereLeaveLevel);
            config.getIntValue(minAsteroidSpawnTime);
            config.getIntValue(maxAsteroidSpawnTime);
            config.getDoubleValue(explosionSpeedThreshold);
            config.getFloatValue(vehicleExplosionMultiplier);
            config.getDoubleValue(vehicleGravity);
        } else {
            config.initEmptyJson();

            config.setFloatValue(orbitLevelGravity);
            config.setIntValue(atmosphereLeaveLevel);
            config.setIntValue(minAsteroidSpawnTime);
            config.setIntValue(maxAsteroidSpawnTime);
            config.setDoubleValue(explosionSpeedThreshold);
            config.setFloatValue(vehicleExplosionMultiplier);
            config.setDoubleValue(vehicleGravity);

            config.saveConfigFile();
        }
    }
}