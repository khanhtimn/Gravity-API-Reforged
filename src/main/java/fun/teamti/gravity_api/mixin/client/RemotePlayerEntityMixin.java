package fun.teamti.gravity_api.mixin.client;

import net.minecraft.client.player.RemotePlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RemotePlayer.class)
public abstract class RemotePlayerEntityMixin {
    // @Override
    // public Direction gravitychanger$getGravityDirection() {
    //     return this.gravitychanger$getTrackedGravityDirection();
    // }
//
    // @Override
    // public void gravitychanger$setGravityDirection(Direction gravityDirection, boolean initialGravity) {
    //     this.gravitychanger$setTrackedGravityDirection(gravityDirection);
    // }
}
