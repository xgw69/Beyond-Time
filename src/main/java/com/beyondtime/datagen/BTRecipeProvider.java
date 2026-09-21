package com.beyondtime.datagen;

import java.util.concurrent.CompletableFuture;

import com.beyondtime.BeyondTime;
import com.beyondtime.registry.BTItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Crafting and machine recipes of Beyond-Time.
 *
 * <p>Recipes are registered here as code rather than hand written JSON, so that a typo in a pattern
 * fails the build instead of silently producing an unusable recipe. The human readable form of every
 * recipe is kept in {@code docs/RECIPES.md}.
 */
public class BTRecipeProvider extends RecipeProvider {
    public BTRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // R-01 microscope, 3x3 crafting table.
        //   . A .      A = amethyst shard
        //   G A D      G = glass, D = diamond
        //   C I C      C = copper ingot, I = iron ingot
        this.shaped(RecipeCategory.MISC, BTItems.MICROSCOPE.get())
                .pattern(" A ")
                .pattern("GAD")
                .pattern("CIC")
                .define('A', Items.AMETHYST_SHARD)
                .define('G', Items.GLASS)
                .define('D', Items.DIAMOND)
                .define('C', Items.COPPER_INGOT)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), this.has(Items.AMETHYST_SHARD))
                .save(this.output);

        // R-02 petri dish, stonecutter: 1 glass -> 2 dishes.
        //
        // Not stonecutterResultFromBase: that helper saves the recipe under an id without a namespace,
        // which silently puts it in the minecraft namespace instead of ours.
        SingleItemRecipeBuilder.stonecutting(
                        Ingredient.of(Items.GLASS), RecipeCategory.MISC, BTItems.PETRI_DISH.get(), 2)
                .unlockedBy(getHasName(Items.GLASS), this.has(Items.GLASS))
                .save(this.output, id("petri_dish_from_glass_stonecutting"));
    }

    private static ResourceKey<Recipe<?>> id(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(BeyondTime.MODID, path));
    }

    /** Data provider wrapper that the data generator entry point instantiates. */
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new BTRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Beyond-Time recipes";
        }
    }
}
