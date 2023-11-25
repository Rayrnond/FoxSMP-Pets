package com.reflexian.foxsmp.features.candy;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.inventory.InvLang;
import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Arrays;
import java.util.List;

public class PetCandyItem {

    private ItemStack petCandyItem;

    public PetCandyItem(ConfigurationSection section) {
        Material candyMaterial = Material.getMaterial(section.getString("material", "CARROT"));
        String candyName = InvLang.format(section.getString("name", "&dPet Candy"));
        List<String> candyLore = section.getStringList("lore").stream().map(InvLang::format).toList();
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

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey("foxsmp", "petcandy"), petCandyItem);
        setRecipeFromConfig(section.getConfigurationSection("recipe"), recipe);
        Bukkit.getServer().addRecipe(recipe);
        FoxSMP.getInstance().getLogger().info("Registered pet candy recipe: " + recipe.getKey());

        Bukkit.getScheduler().runTaskTimer(FoxSMP.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasDiscoveredRecipe(recipe.getKey())) {
                    player.discoverRecipe(recipe.getKey());
                }
            }
        }, 5, 4);

        Listener debugListener = new Listener() {

            @EventHandler
            public void onCraftInterfere(PrepareItemCraftEvent event) {
                Arrays.stream(event.getHandlers().getRegisteredListeners()).forEach(listener -> {
                    String pluginName = listener.getPlugin().getName();
                    System.out.println(pluginName);
                });
            }

            @EventHandler
            public void onPreCraftInterfere(CraftItemEvent event) {
                Arrays.stream(event.getHandlers().getRegisteredListeners()).forEach(listener -> {
                    String pluginName = listener.getPlugin().getName();
                    System.out.println(pluginName);
                });
            }


        };

        //Bukkit.getServer().getPluginManager().registerEvents(debugListener, FoxSMP.getInstance());

        Listener listener = new Listener() {
            @EventHandler
            public void onInteractWithCandy(PlayerInteractEvent event) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    ItemStack item = event.getItem();
                    if (item == null) return;
                    if (event.getHand() == null) return;
                    if (item.getType() != candyMaterial) return;

                    NBT.get(item, nbt -> {
                        if (nbt.hasTag("petcandy")) {
                            Player player = event.getPlayer();

                            PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(),null);
                            if (playerData == null || !playerData.hasPet()) {
                                player.sendMessage("§cYou don't have a pet!");
                                return;
                            } else if (playerData.getPet().getLevel() == 100) {
                                player.sendMessage("§cYour pet is already level 100!");
                                return;
                            }

                            player.sendMessage("§eYou used pet candy!");

                            int amount = 1;
                            if (player.isSneaking()) {
                                amount=item.getAmount();
                            }

                            for (int i = 0; i < amount; i++) {
                                if (item.getAmount() > 1) {
                                    item.setAmount(item.getAmount() - 1);
                                    player.getInventory().setItem(event.getHand(), item);
                                } else {
                                    player.getInventory().setItem(event.getHand(), null);
                                    break;
                                }
                            }
                            int expPer = FoxSMP.getInstance().getConfig().getInt("pet-candy.xp",200);
                            amount = amount * expPer;
                            playerData.getPet().setXp(playerData.getPet().getXp() + amount);
                            player.sendMessage("§aYour pet gained §e"+amount+"§a XP!");
                            player.sendMessage("§aYour pet is now level §e"+playerData.getPet().getLevel()+"§a!");

                            PlayerData.save(playerData);

                            if (playerData.getPet() instanceof NorthernNomadPet pet) {
                                pet.updateSpeed(player, false);
                            }
                        }
                    });
                }
            }
        };
        FoxSMP.getInstance().getServer().getPluginManager().registerEvents(listener, FoxSMP.getInstance());
    }

    public void givePlayerCandy(Player player, int amount) {
        ItemStack candy = petCandyItem.clone();
        candy.setAmount(amount);
        player.getInventory().addItem(candy);
    }

    private static void setRecipeFromConfig(ConfigurationSection config, ShapedRecipe recipe) {
        // Define the crafting grid shape based on the keys
        String[] shape = new String[]{"ABC", "DEF", "GHI"};
        recipe.shape(shape);
        for (int i = 0; i < shape.length; i++) {
            String row = shape[i];
            StringBuilder newRow = new StringBuilder();
            for (char recipeElement : row.toCharArray()) {
                String key = config.getString(Integer.toString(i * 3 + row.indexOf(recipeElement)));
                if (key != null && !key.isEmpty()) {
                    Material ingredientMaterial = Material.getMaterial(key);
                    if (ingredientMaterial != null) {
                        recipe.setIngredient(recipeElement, ingredientMaterial);
                    } else {
                        recipe.setIngredient(recipeElement, Material.AIR);
                        System.out.println("Invalid Material name: " + key);
                    }
                } else {
                    recipe.setIngredient(recipeElement, Material.AIR);
                    System.out.println("No key found for slot " + recipeElement);
                }
                newRow.append(recipeElement);
            }
            shape[i] = newRow.toString();
        }

        // Set the modified shape
        recipe.shape(shape);
    }

}
