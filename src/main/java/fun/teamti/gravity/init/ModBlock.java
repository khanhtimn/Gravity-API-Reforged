package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.block.GravityPlatingBlock;
import fun.teamti.gravity.block.GravityPlatingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GravityMod.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GravityMod.MOD_ID);

    public static final RegistryObject<Block> PLATING_BLOCK = BLOCKS.register("plating",
            () -> new GravityPlatingBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission().instabreak()));

    public static final RegistryObject<BlockEntityType<GravityPlatingBlockEntity>> PLATING_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("plating_block_entity", () ->
                    BlockEntityType.Builder.of(
                                    GravityPlatingBlockEntity::new,
                                    PLATING_BLOCK.get())
                            .build(null));
}
