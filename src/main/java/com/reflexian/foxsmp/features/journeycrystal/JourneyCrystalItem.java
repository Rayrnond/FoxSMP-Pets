package com.reflexian.foxsmp.features.journeycrystal;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.inventories.JourneyCrystalGUI;
import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.inventory.InvLang;
import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import de.tr7zw.nbtapi.NBT;
import me.lucko.helper.Events;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class JourneyCrystalItem {

    private ItemStack journeyCrystalItem;

    public JourneyCrystalItem(ConfigurationSection section) {
        Material crystalMaterial = Material.getMaterial(section.getString("material", "DIAMOND"));
        String crystalName = InvLang.format(section.getString("name", "&dJourney Crystal"));
        List<String> crystalLore = section.getStringList("lore").stream().map(InvLang::format).toList();
        int model = section.getInt("model", 0);

        if (crystalMaterial == null) {
            Bukkit.getLogger().warning("Invalid material for journey crystal: " + section.getString("material"));
            return;
        }

        journeyCrystalItem = new ItemBuilder(crystalMaterial)
                .setName(crystalName)
                .setLore(crystalLore)
                .setModel(model)
                .build();

        FoxSMP.getInstance().getLogger().info("Registered journey crystal item: " + journeyCrystalItem);

        NBT.modify(journeyCrystalItem, item -> {
            item.setBoolean("journeyCrystalItem", true);
        });

        //ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey("foxsmp", "petcandy"), petCandyItem);
        //Bukkit.getServer().addRecipe(recipe);

        Events.subscribe(PlayerInteractEvent.class)
                .handler(event -> {
                   if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                       ItemStack item = event.getItem();
                       if (item == null) return;
                       if (event.getHand() == null) return;
                       if (item.getType() != crystalMaterial) return;

                       NBT.get(item, nbt -> {
                          if (nbt.hasTag("journeyCrystalItem")) {
                              Player player = event.getPlayer();

                              PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(),null);
                              if (playerData == null || !playerData.hasPet()) {
                                    player.sendMessage("§cYou don't have a pet!");
                                  return;
                              }

                              if (playerData.hasPet()) {
                                  playerData.getPet().getBalloon().kill();
                              }
                              playerData.setPet(null);
                              PlayerData.save(playerData);
                              player.sendMessage("§eYou used journey crystal!");

                              if (item.getAmount() > 1) {
                                  item.setAmount(item.getAmount() - 1);
                                  player.getInventory().setItem(event.getHand(), item);
                              } else {
                                  player.getInventory().setItem(event.getHand(), null);
                              }

                              new JourneyCrystalGUI().init(event.getPlayer());
                          }
                       });
                   }
                });
    }

    public void givePlayerCrystal(Player player, int amount) {
        ItemStack candy = journeyCrystalItem.clone();
        candy.setAmount(amount);
        player.getInventory().addItem(candy);
    }


}
