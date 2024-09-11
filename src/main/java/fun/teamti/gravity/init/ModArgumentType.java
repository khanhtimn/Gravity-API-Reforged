package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.command.argument.DirectionArgumentType;
import fun.teamti.gravity.command.argument.LocalDirectionArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModArgumentType {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, GravityMod.MOD_ID);

    public static final RegistryObject<ArgumentTypeInfo<DirectionArgumentType, ?>> DIRECTION_ARGUMENT = ARGUMENT_TYPES.register(
            "direction",
            () -> ArgumentTypeInfos.registerByClass(DirectionArgumentType.class, SingletonArgumentInfo.contextFree(() -> DirectionArgumentType.INSTANCE))
    );

    public static final RegistryObject<ArgumentTypeInfo<LocalDirectionArgumentType, ?>> LOCAL_DIRECTION_ARGUMENT = ARGUMENT_TYPES.register(
            "local_direction",
            () -> ArgumentTypeInfos.registerByClass(LocalDirectionArgumentType.class, SingletonArgumentInfo.contextFree(() -> LocalDirectionArgumentType.INSTANCE))
    );


}
