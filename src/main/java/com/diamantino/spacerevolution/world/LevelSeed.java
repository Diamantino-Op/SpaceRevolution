package com.diamantino.spacerevolution.world;

import com.diamantino.spacerevolution.client.dimension.renderer.StarInformation;

public class LevelSeed {

    private static long seed = 0;

    public static long getSeed() {
        return seed;
    }

    public static void setSeed(long seed) {
        LevelSeed.seed = seed;
        StarInformation.STAR_CACHE.clear();
    }
}