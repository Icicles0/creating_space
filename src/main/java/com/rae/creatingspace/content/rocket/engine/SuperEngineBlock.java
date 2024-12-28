package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.api.multiblock.MBController;
import com.rae.creatingspace.api.multiblock.MBShape;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.*;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@NonnullDefault
public class SuperEngineBlock extends MBController implements IBE<RocketEngineBlockEntity.NbtDependent> {

    public static final EnumProperty<Power> POWER_PACK = EnumProperty.create("power_pack",Power.class);
    public static final EnumProperty<Exhaust> EXHAUST_PACK = EnumProperty.create("exhaust", Exhaust.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private final MBShape shapes;
    public SuperEngineBlock(Properties properties, DirectionalBlock structure) {
        super(properties, structure);
        shapes = new MBShape(structure, new Vec3i(1, 3,1), new Vec3i(0,1,0),
                new HashMap<>(Map.of(
                        Direction.NORTH, List.of(List.of(List.of(Direction.UP)),List.of(List.of(Direction.NORTH)),List.of(List.of(Direction.DOWN))),
                        Direction.SOUTH, List.of(List.of(List.of(Direction.UP)),List.of(List.of(Direction.NORTH)),List.of(List.of(Direction.DOWN))),
                        Direction.WEST, List.of(List.of(List.of(Direction.UP)),List.of(List.of(Direction.NORTH)),List.of(List.of(Direction.DOWN))),
                        Direction.EAST, List.of(List.of(List.of(Direction.UP)),List.of(List.of(Direction.NORTH)),List.of(List.of(Direction.DOWN)))
                )
        ));
        this.registerDefaultState(this.defaultBlockState().setValue(EXHAUST_PACK, Exhaust.BELL_NOZZLE).setValue(ACTIVE, Boolean.TRUE)
                .setValue(FACING,Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXHAUST_PACK).add(ACTIVE).add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(EXHAUST_PACK, Exhaust.BELL_NOZZLE).setValue(ACTIVE, Boolean.TRUE)
                .setValue(FACING,context.getHorizontalDirection().getOpposite());
    }

    @Override
    public Class<RocketEngineBlockEntity.NbtDependent> getBlockEntityClass() {
        return RocketEngineBlockEntity.NbtDependent.class;
    }

    @Override
    public BlockEntityType<? extends RocketEngineBlockEntity.NbtDependent> getBlockEntityType() {
        return BlockEntityInit.NBT_DEPENDENT_ENGINE.get();
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);

        if (worldIn.isClientSide)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            be.setFromNbt(stack.getOrCreateTag().getCompound("blockEntity"));
        });
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull BlockState state) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        Optional<RocketEngineBlockEntity.NbtDependent> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);

        CompoundTag tag = stack.getOrCreateTag();
        assert blockEntityOptional.orElse(null) != null;
        CompoundTag beData = blockEntityOptional.orElse(null).saveWithoutMetadata();
        tag.put("blockEntity", beData);
        stack.setTag(tag);
        return stack;
    }

    @Override
    public void tick(@NotNull BlockState pState, ServerLevel pLevel, BlockPos pPos, @NotNull RandomSource pRandom) {
        /*irection targetSide = Direction.DOWN;
        BlockPos structurePos = pPos.relative(targetSide);
        BlockState occupiedState = pLevel.getBlockState(structurePos);
        BlockState requiredStructure = BlockInit.ENGINE_STRUCTURAL.getDefaultState()
                .setValue(SuperRocketStructuralBlock.FACING, targetSide.getOpposite());
        pLevel.setBlockAndUpdate(structurePos, requiredStructure);*/
        shapes.repairStructure(pLevel, pPos, Direction.NORTH);

        //make the same for big engine block

    }

    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState newBlockState, boolean isMoving) {
        super.onRemove(blockState, level, blockPos, newBlockState, isMoving);
        IBE.onRemove(blockState, level, blockPos, newBlockState);
    }

    @Override
    public boolean skipRendering(@NotNull BlockState p_60532_, @NotNull BlockState p_60533_, @NotNull Direction p_60534_) {
        return true;
    }

    public Vec3i getPlaceOffset(Direction facing) {
        return switch (facing){
            case DOWN -> new Vec3i(0,0,0);
            default -> new Vec3i(0,1,0);
        };
    }

    public enum Power implements StringRepresentable {
        STANDARD;
        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }
    public enum Exhaust  implements StringRepresentable {
        BELL_NOZZLE, AEROSPIKE;
        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
