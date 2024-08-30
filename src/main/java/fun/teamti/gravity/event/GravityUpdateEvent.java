package fun.teamti.gravity.event;

import fun.teamti.gravity.capability.data.GravityData;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;


public class GravityUpdateEvent extends Event {
    private final Entity entity;
    private final GravityData gravityData;

    public GravityUpdateEvent(Entity entity, GravityData gravityData) {
        this.entity = entity;
        this.gravityData = gravityData;
    }

    public Entity getEntity() {
        return entity;
    }

    public GravityData getGravityData() {
        return gravityData;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}