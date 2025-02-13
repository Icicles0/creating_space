package com.rae.creatingspace.mixin.worldgen;

import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RandomState.class)
public class RandomStateMixin {
    //@Inject(method = "apply")
    private static void provideNoise(){

    }
}
