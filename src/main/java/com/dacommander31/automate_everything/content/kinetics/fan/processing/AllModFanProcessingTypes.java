package com.dacommander31.automate_everything.content.kinetics.fan.processing;

import com.dacommander31.automate_everything.AllModTags.AllModBlockTags;
import com.dacommander31.automate_everything.AllModTags.AllModFluidTags;
import com.dacommander31.automate_everything.recipe.FreezingRecipe;
import com.dacommander31.automate_everything.recipe.ModRecipeType;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AllModFanProcessingTypes {

    public static final FreezingType FREEZING = register("freezing", new FreezingType());

    private static final Map<String, FanProcessingType> LEGACY_NAME_MAP;

    static {
        Object2ReferenceOpenHashMap<String, FanProcessingType> map = new Object2ReferenceOpenHashMap<>();
        map.put("FREEZING", FREEZING);
        map.trim();
        LEGACY_NAME_MAP = map;
    }

    private static <T extends FanProcessingType> T register(String id, T type) {
        FanProcessingTypeRegistry.register(Create.asResource(id), type);
        return type;
    }

    @Nullable
    public static FanProcessingType ofLegacyName(String name) {
        return LEGACY_NAME_MAP.get(name);
    }

    public static FanProcessingType parseLegacy(String str) {
        FanProcessingType type = ofLegacyName(str);
        if (type != null) {
            return type;
        }
        return FanProcessingType.parse(str);
    }

    public static void register() {
    }

    public static class FreezingType implements FanProcessingType {
        private static final RecipeWrapper RECIPE_WRAPPER = new RecipeWrapper(new ItemStackHandler(1));

        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            FluidState fluidState = level.getFluidState(pos);
            if (AllModFluidTags.FAN_PROCESSING_CATALYSTS_FREEZING.matches(fluidState)) {
                return true;
            }
            BlockState blockState = level.getBlockState(pos);
            if (AllModBlockTags.FAN_PROCESSING_CATALYSTS_FREEZING.matches(blockState)) {
                return true;
            }
            return false;
        }

        @Override
        public int getPriority() {
            return 50;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            RECIPE_WRAPPER.setItem(0, stack);
            Optional<FreezingRecipe> freezingRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipeType.FREEZING, RECIPE_WRAPPER, level)
                    .filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            if (freezingRecipe.isPresent())
                return true;

            return true;
        }

        @Override
        public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
            RECIPE_WRAPPER.setItem(0, stack);
            Optional<FreezingRecipe> freezingRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipeType.FREEZING, RECIPE_WRAPPER, level)
                    .filter(AllRecipeTypes.CAN_BE_AUTOMATED);

            if (freezingRecipe.isPresent()) {
                RegistryAccess registryAccess = level.registryAccess();
                return RecipeApplier.applyRecipeOn(level, stack, freezingRecipe.get());
            }
            return Collections.emptyList();
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0)
                return;
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 0, 1 / 16f, 0);
        }

        @Override
        public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
            particleAccess.setColor(Color.mixColors(0xFF4400, 0xFF8855, random.nextFloat()));
            particleAccess.setAlpha(.5f);
            if (random.nextFloat() < 1 / 32f)
                particleAccess.spawnExtraParticle(ParticleTypes.SNOWFLAKE, .25f);
            if (random.nextFloat() < 1 / 16f)
                particleAccess.spawnExtraParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.POWDER_SNOW.defaultBlockState()), .25f);
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide)
                return;

            if (entity.canFreeze()) {
                entity.setIsInPowderSnow(true);
            }
        }
    }
}
