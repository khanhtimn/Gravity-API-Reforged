package fun.teamti.gravity.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    // use Comparable<Direction> instead of Direction because of erased signature
    @WrapOperation(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
                    ordinal = 0
            )
    )
    private Comparable<Direction> wrapOperation_onLandedUpon_get_0(
            BlockState blockState, Property<Direction> property,
            Operation<Comparable<Direction>> original,
            Level world, BlockState state, BlockPos pos,
            Entity entity, float fallDistance
    ) {
        var originalResult = original.call(blockState, property);
        Direction gravityDirection = GravityAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return originalResult;
        }

        return original.call(blockState, property) == gravityDirection.getOpposite() ? Direction.UP : Direction.DOWN;
    }
}
