package fun.teamti.gravity.item;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GravityChangerItemAOE extends Item {

    public final Direction gravityDirection;
    
    public GravityChangerItemAOE(Properties properties, Direction pDirection) {
        super(properties);
        gravityDirection = pDirection;
    }
    
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player user, @NotNull InteractionHand hand) {
        if (!world.isClientSide()) {
            //TODO: Enchantments?
            AABB box = user.getBoundingBox().inflate(3);
            List<Entity> list = world.getEntitiesOfClass(Entity.class, box, e -> !(e instanceof Player));
            for (Entity entity : list) {
                GravityAPI.setBaseGravityDirection(entity, gravityDirection);
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
}
