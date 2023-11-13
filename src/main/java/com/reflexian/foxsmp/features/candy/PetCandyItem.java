package com.reflexian.foxsmp.features.candy;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import de.tr7zw.nbtapi.NBT;
import me.lucko.helper.Events;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class PetCandyItem {

    private ItemStack petCandyItem;

    public PetCandyItem(ConfigurationSection section) {
        Material candyMaterial = Material.getMaterial(section.getString("material", "CARROT"));
        String candyName = section.getString("name", "&dPet Candy");
        List<String> candyLore = section.getStringList("lore");
        int model = section.getInt("model", 0);

        if (candyMaterial == null) {
            Bukkit.getLogger().warning("Invalid material for pet candy: " + section.getString("material"));
            return;
        }

        petCandyItem = new ItemBuilder(candyMaterial)
                .setName(candyName)
                .setLore(candyLore)
                .setModel(model)
                .build();

        FoxSMP.getInstance().getLogger().info("Registered pet candy item: " + petCandyItem);

        NBT.modify(petCandyItem, item -> {
            item.setBoolean("petcandy", true);
        });

        //ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey("foxsmp", "petcandy"), petCandyItem);
        //Bukkit.getServer().addRecipe(recipe);

        Events.subscribe(PlayerInteractEvent.class)
                .handler(event -> {
                   if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                       ItemStack item = event.getItem();
                       if (item == null) return;
                       if (event.getHand() == null) return;
                       if (item.getType() != candyMaterial) return;

                       NBT.get(item, nbt -> {
                          if (nbt.hasTag("petcandy")) {
                              Player player = event.getPlayer();
                              player.sendMessage("You used pet candy!");

                              if (item.getAmount() > 1) {
                                  item.setAmount(item.getAmount() - 1);
                                  player.getInventory().setItem(event.getHand(), item);
                              } else {
                                  player.getInventory().setItem(event.getHand(), null);
                              }
                              // TODO add to player's exp here
                          }
                       });
                   }
                });
    }

    public void givePlayerCandy(Player player, int amount) {
        ItemStack candy = petCandyItem.clone();
        candy.setAmount(amount);
        player.getInventory().addItem(candy);
    }

    private static void setRecipeFromConfig(ConfigurationSection config, ShapedRecipe recipe) {
        // Define the crafting grid shape based on the keys
        String[] shape = new String[]{"ABC", "DEF", "GHI"};

        for (int i = 0; i < shape.length; i++) {
            String row = shape[i];
            StringBuilder newRow = new StringBuilder();
            for (char c : row.toCharArray()) {
                String key = config.getString(Integer.toString(i * 3 + row.indexOf(c)));
                if (key != null && !key.isEmpty()) {
                    Material ingredientMaterial = Material.getMaterial(key);
                    if (ingredientMaterial != null) {
                        recipe.setIngredient(c, ingredientMaterial);
                    }
                }
                newRow.append(c);
            }
            shape[i] = newRow.toString();
        }

        // Set the modified shape
        recipe.shape(shape);
    }

}
