package com.rae.creatingspace.api.multiblock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;

public abstract class MBController extends Block {
    final DirectionalBlock structure;
    protected MBController(Properties properties, DirectionalBlock structure) {
        super(properties);
        this.structure = structure;
    }

    public DirectionalBlock getStructure() {
        return structure;
    }
}