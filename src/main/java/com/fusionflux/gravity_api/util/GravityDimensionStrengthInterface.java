package com.fusionflux.gravity_api.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.util.math.Direction;

public interface GravityDimensionStrengthInterface extends Component, AutoSyncedComponent {


    double getDimensionGravityStrength();
    void setDimensionGravityStrength(double strength);
}
