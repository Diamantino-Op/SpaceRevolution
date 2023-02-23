package com.diamantino.spacerevolution.initialization;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static ItemGroup machines;
    public static ItemGroup cables;
    public static ItemGroup blocks;

    public static void registerItemGroups() {
        machines = FabricItemGroup.builder(new Identifier(ModReferences.modId, "machines_group")).displayName(Text.literal("Space Revolution Machines")).icon(() -> new ItemStack(ModBlocks.crusherBlock)).build();
        cables = FabricItemGroup.builder(new Identifier(ModReferences.modId, "cables_group")).displayName(Text.literal("Space Revolution Cables")).icon(() -> new ItemStack(ModBlocks.electricCables.get(0))).build();
        blocks = FabricItemGroup.builder(new Identifier(ModReferences.modId, "blocks_groups")).displayName(Text.literal("Space Revolution Blocks")).icon(() -> new ItemStack(ModBlocks.asteroidBlock)).build();

        ModReferences.logger.debug("Registering ModItemGroups for " + ModReferences.modId);
    }
}
