package com.rae.creatingspace.mixin.fluids.pump;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.simibubi.create.content.fluids.FluidPropagator.*;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {

    @Inject(method = "propagateChangedPipe" , at= @At(value = "HEAD"),cancellable = true, remap = false)
    private static void propagateForIsolatedFluidPump(LevelAccessor world, BlockPos pipePos, BlockState pipeState, CallbackInfo ci){
        List<Pair<Integer, BlockPos>> frontier = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<Pair<PumpBlockEntity, Direction>> discoveredPumps = new HashSet<>();

        frontier.add(Pair.of(0, pipePos));

        // Visit all connected pumps to update their network
        while (!frontier.isEmpty()) {
            Pair<Integer, BlockPos> pair = frontier.remove(0);
            BlockPos currentPos = pair.getSecond();
            if (visited.contains(currentPos))
                continue;
            visited.add(currentPos);
            BlockState currentState = currentPos.equals(pipePos) ? pipeState : world.getBlockState(currentPos);
            FluidTransportBehaviour pipe = getPipe(world, currentPos);
            if (pipe == null)
                continue;
            pipe.wipePressure();

            for (Direction direction : getPipeConnections(currentState, pipe)) {
                BlockPos target = currentPos.relative(direction);
                if (world instanceof Level l && !l.isLoaded(target))
                    continue;

                BlockEntity blockEntity = world.getBlockEntity(target);
                BlockState targetState = world.getBlockState(target);
                if (blockEntity instanceof PumpBlockEntity) {
                    if (!(BlockInit.ISOLATED_FLUID_PUMP.has(targetState) || AllBlocks.MECHANICAL_PUMP.has(targetState))|| targetState.getValue(PumpBlock.FACING)
                            .getAxis() != direction.getAxis())
                        continue;
                    discoveredPumps.add(Pair.of((PumpBlockEntity) blockEntity, direction.getOpposite()));
                    continue;
                }
                if (visited.contains(target))
                    continue;
                FluidTransportBehaviour targetPipe = getPipe(world, target);
                if (targetPipe == null)
                    continue;
                Integer distance = pair.getFirst();
                if (distance >= getPumpRange() && !targetPipe.hasAnyPressure())
                    continue;
                if (targetPipe.canHaveFlowToward(targetState, direction.getOpposite()))
                    frontier.add(Pair.of(distance + 1, target));
            }
        }

        discoveredPumps.forEach(pair -> pair.getFirst()
                .updatePipesOnSide(pair.getSecond()));
        ci.cancel();
    }
}
