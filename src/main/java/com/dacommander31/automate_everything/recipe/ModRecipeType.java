package com.dacommander31.automate_everything.recipe;

import com.dacommander31.automate_everything.AutomateEverything;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

public interface ModRecipeType<T extends Recipe<?>> {
    RecipeType<FreezingRecipe> FREEZING = register("freezing");

    static <T extends Recipe<?>> RecipeType<T> register(final String pIdentifier) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(AutomateEverything.MODID, pIdentifier), new RecipeType<T>() {
            public String toString() {
                return pIdentifier;
            }
        });
    }
}
