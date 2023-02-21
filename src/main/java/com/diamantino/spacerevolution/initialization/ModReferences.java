package com.diamantino.spacerevolution.initialization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModReferences {
    public static final String modId = "spacerevolution";
    public static final Logger logger = LogManager.getLogger(modId);

    public static void registerModReferences() {
        logger.debug("Registering ModReferences for " + modId);
    }
}
