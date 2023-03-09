package com.diamantino.spacerevolution.config;

import com.diamantino.spacerevolution.initialization.ModReferences;

import java.nio.file.Path;

public class SpaceRevolutionCommonConfigs {
    private static final Config config = new Config(Path.of("config/spacerevolution/common.json"));

    //Config values
    public static ConfigValue.FloatValue orbitLevelGravity = new ConfigValue.FloatValue("orbit_level_gravity", 0.0f);
    public static ConfigValue.IntValue atmosphereLeaveLevel = new ConfigValue.IntValue("atmosphere_leave_level", 500);

    public static void initModConfigs() {
        ModReferences.logger.debug("Initializing ModConfigs for " + ModReferences.modId);

        if (config.configFileExists()) {
            config.loadConfigFile();

            config.getFloatValue(orbitLevelGravity);
            config.getIntValue(atmosphereLeaveLevel);
        } else {
            config.initEmptyJson();

            config.setFloatValue(orbitLevelGravity);
            config.setIntValue(atmosphereLeaveLevel);

            config.saveConfigFile();
        }
    }
}