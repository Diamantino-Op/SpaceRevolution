package com.diamantino.spacerevolution.initialization;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static ItemGroup machines;

    public static void registerItemGroups() {
        machines = FabricItemGroup.builder(new Identifier(ModReferences.modId, "machines_group")).displayName(Text.literal("Space Revolution Machines")).icon(() -> new ItemStack(ModBlocks.crusherBlock)).build();

        ModReferences.logger.debug("Registering ModItemGroups for " + ModReferences.modId);
    }
}
