package fun.teamti.gravity.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class GravityUpdateEvent extends EntityEvent {

    public GravityUpdateEvent(Entity entity) {
        super(entity);
    }
}
