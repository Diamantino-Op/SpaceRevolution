package com.diamantino.spacerevolution.config;

import com.diamantino.spacerevolution.initialization.ModReferences;

import java.nio.file.Path;

public class SpaceRevolutionCommonConfigs {
    private static final Config config = new Config(Path.of("config/spacerevolution/common.json"));

    //Config values
    public static ConfigValue.FloatValue orbitLevelGravity = new ConfigValue.FloatValue("orbit_level_gravity", 0.0f);
    public static int atmosphereLeaveLevel = 500;

    public static void initModConfigs() {
        ModReferences.logger.debug("Initializing ModConfigs for " + ModReferences.modId);

        if (config.configFileExists()) {

        } else {

        }
    }
}