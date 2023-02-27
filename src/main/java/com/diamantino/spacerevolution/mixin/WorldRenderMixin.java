package com.diamantino.spacerevolution.mixin;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRenderMixin {
    @Accessor("ticks")
    int getTicks();

    @Accessor("lastCloudsBlockX")
    int getLastCloudsBlockX();
    @Accessor("lastCloudsBlockX")
    void setLastCloudsBlockX(int lastCloudsBlockX);

    @Accessor("lastCloudsBlockY")
    int getLastCloudsBlockY();
    @Accessor("lastCloudsBlockY")
    void setLastCloudsBlockY(int lastCloudsBlockY);

    @Accessor("lastCloudsBlockZ")
    int getLastCloudsBlockZ();
    @Accessor("lastCloudsBlockZ")
    void setLastCloudsBlockZ(int lastCloudsBlockZ);

    @Accessor("lastCloudsColor")
    Vec3d getLastCloudsColor();
    @Accessor("lastCloudsColor")
    void setLastCloudsColor(Vec3d lastCloudsColor);

    @Accessor("lastCloudRenderMode")
    CloudRenderMode getLastCloudRenderMode();
    @Accessor("lastCloudRenderMode")
    void setLastCloudRenderMode(CloudRenderMode lastCloudRenderMode);

    @Accessor("cloudsDirty")
    boolean getCloudsDirty();
    @Accessor("cloudsDirty")
    void setCloudsDirty(boolean cloudsDirty);

    @Accessor("cloudsBuffer")
    VertexBuffer getCloudsBuffer();
    @Accessor("cloudsBuffer")
    void setCloudsBuffer(VertexBuffer cloudsBuffer);

    @Invoker("renderClouds")
    BufferBuilder.BuiltBuffer invokeRenderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color);

}
