package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.server.blockentities.MechanicalElectrolyzerBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MechanicalElectrolyzerBlock extends HorizontalKineticBlock implements IBE<MechanicalElectrolyzerBlockEntity> {

	public MechanicalElectrolyzerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext
				&& ((EntityCollisionContext) context).getEntity() instanceof Player)
			return AllShapes.CASING_14PX.get(Direction.DOWN);

		return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
    public Class<MechanicalElectrolyzerBlockEntity> getBlockEntityClass() {
        return MechanicalElectrolyzerBlockEntity.class;
	}

	@Override
    public BlockEntityType<? extends MechanicalElectrolyzerBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ELECTROLIZER.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}
/*
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {

		ItemStack held = player.getMainHandItem();
		if (!held.isEmpty() && (held.getItem() instanceof ElectrodeItem)) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
					() -> () -> withBlockEntityDo(level, pos, be -> be.setElectrode(held)));
			return InteractionResult.SUCCESS;
		} else if (held.isEmpty()) {
			player.setItemInHand(InteractionHand.MAIN_HAND, ((MechanicalElectrolyzerBlockEntity) level.getBlockEntity(pos)).getElectrode());
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
					() -> () -> withBlockEntityDo(level, pos, be -> be.setElectrode(null)));
		}
		return InteractionResult.PASS;

	}
 */
}