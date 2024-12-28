package com.rae.creatingspace.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;

public class MBShape {
    //default is north
    private final DirectionalBlock structure;
    private HashMap<Direction, List<List<List<Direction>>>> shapes;// X, Y, Z
    private final Vec3i defaultOffset;
    private final Vec3i defaultSize;

    public MBShape(DirectionalBlock structure,  Vec3i defaultSize,  Vec3i defaultOffset,HashMap<Direction, List<List<List<Direction>>>> shapes) {
        this.structure = structure;
        this.defaultOffset = defaultOffset;
        this.defaultSize = defaultSize;
        this.shapes = shapes;
    }

    public Vec3i getOffset(Direction facing){
        return switch (facing.getAxis()){
            case Z -> new Vec3i(defaultOffset.getZ(), defaultOffset.getY(), defaultOffset.getX());
            case Y -> new Vec3i(defaultOffset.getY(), defaultOffset.getX(),defaultOffset.getZ());
            default -> defaultOffset;
        };
    }
    public List<List<List<Direction>>> getShape(Direction facing){
        return shapes.get(facing);
    }
    public Vec3i getSize(Direction facing){
        return switch (facing.getAxis()){
            case Z -> new Vec3i(defaultSize.getZ(), defaultSize.getY(), defaultSize.getX());
            case Y -> new Vec3i(defaultSize.getY(), defaultSize.getX(),defaultSize.getZ());
            default -> defaultSize;
        };
    }
    /**
     * repair or place the structure blocks
     */
    public void repairStructure(Level level, BlockPos controlPos, Direction facing){
        List<List<List<Direction>>> shape = getShape(facing);
        Vec3i off = getOffset(facing);
        Vec3i size = getSize(facing);
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (x!=0 && y!=0 && z!=0) {
                        //verify
                        BlockState structureShape = level.getBlockState(controlPos
                                .offset(
                                        x-off.getX(),y-off.getY(),z-off.getZ()
                                ));
                        if (!structureShape.is(structure) || structureShape.getValue(DirectionalBlock.FACING) != shape.get(x).get(y).get(z)){
                            level.setBlockAndUpdate(
                                    controlPos
                                            .offset(
                                                    x-off.getX(),y-off.getY(),z-off.getZ()
                                            ),
                                    structure.defaultBlockState().setValue(DirectionalBlock.FACING,shape.get(x).get(y).get(z))
                            );
                        }
                    }
                }
            }
        }
    }
}
