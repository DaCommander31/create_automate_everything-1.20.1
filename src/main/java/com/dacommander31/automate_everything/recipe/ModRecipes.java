package com.dacommander31.automate_everything.recipe;

import com.dacommander31.automate_everything.AutomateEverything;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AutomateEverything.MODID);

    public static final RegistryObject<RecipeSerializer<FreezingRecipe>> FREEZING_SERIALIZER =
            SERIALIZERS.register("freezing", () -> FreezingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
