package com.reflexian.foxsmp.utilities.inventory;

import com.reflexian.foxsmp.FoxSMP;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class InvUtils {

    private static final Map<String, YamlConfiguration> inventories = new HashMap<>();
    public static void init() { // can reuse to reload!
        inventories.clear();
        var instance = FoxSMP.getInstance();
        File files = new File(instance.getDataFolder() + "/inventories");
        if (!files.exists()) {
            files.mkdirs();
        }
        for (File file : files.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                try {
                    inventories.put(file.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
                    instance.getLogger().info("Loaded inventory file " + file.getName() + "!");
                }catch (Exception e) {
                    e.printStackTrace();
                    instance.getLogger().severe("Failed to load inventory file " + file.getName() + "!");
                }
            }
        }

    }

    public static void showInventory(Player player, String fileName, ClickAction... clickActions) {
        showInventory(player,fileName,false,clickActions);
    }

    public static void showInventory(Player player, String fileName, boolean updateSec, ClickAction... clickActions) {
        final var instance = FoxSMP.getInstance();
        YamlConfiguration c = inventories.getOrDefault(fileName,null);
        if (c==null) {
            player.sendMessage("§cSomething has gone wrong, contact an administrator!");
            instance.getLogger().severe("Couldn't find inventory file for " + fileName+"!");
            return;
        }
        int size = c.getInt("size",54);
        InventoryProvider inventoryProvider = new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                for (String key : c.getConfigurationSection("items").getKeys(false)) {
                    String pos = "items."+key;
                    // doesn't have any click actions?

                    Set<Integer> slots = new HashSet<Integer>();
                    if (c.getString(pos+".slots").contains(",")) {
                        for (String s : c.getString(pos + ".slots").split(",")) {
                            slots.add(Integer.parseInt(s));
                        }
                    } else if (c.getString(pos+".slots").equals("-1")) {
                        for (int i = 0; i < size; i++) {
                            slots.add(i);
                        }
                    } else {
                        slots.add(c.getInt(pos+".slots"));
                    }

                    numberloop:
                    for (Integer slot : slots) {
                        if (slot<0) continue;
                        int col=slot%9;
                        int row=((slot-col)/9);

                        if (contains(key, clickActions)) {
                            for (ClickAction clickAction : clickActions) {
                                if (clickAction.isDisabled()&&clickAction.getId().equals(key)) continue numberloop;
                            }
                            contents.set(row,col,ClickableItem.of(InvLang.itemStackInvs(c,"items."+key, player), event ->{
                                for (ClickAction clickAction : clickActions) {
                                    if (clickAction.isDisabled()) continue;
                                    if (clickAction.getId().equals(key)) {
                                        clickAction.getConsumer().accept(player, clickAction);
                                        break;
                                    }
                                }

                                if (c.contains(pos+".sound")) {
                                    player.playSound(player.getLocation(), Sound.valueOf(c.getString(pos+".sound").replaceAll("\\.", "_").toUpperCase()), 100,1);
                                }
                                if (c.contains(pos+".commands")) {
                                    for (String s : c.getStringList(pos + ".commands")) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s.replaceAll("%player%", player.getName()));
                                    }
                                }

                            }));
                        } else {
                            contents.set(row,col,ClickableItem.empty(InvLang.itemStackInvs(c,"items."+key,player)));
                        }
                    }
                }
            }

            @Override
            public void update(Player player, InventoryContents contents) {
                if (updateSec) {
                    int state = contents.property("state", 0);
                    contents.setProperty("state", state + 1);
                    if(state % 20 != 0) {
                        return;
                    }
                    init(player,contents);
                }
            }
        };
        SmartInventory inv = SmartInventory.builder().id(c.getString("title")).provider(inventoryProvider).size((size/9), 9).title(InvLang.format(c.getString("title"))).manager(instance.getInventoryManager()).build();
        inv.open(player);
    }


    private boolean contains(String id, ClickAction... clickAction) {
        for (ClickAction action : clickAction) {
            if (action.getId().equals(id)) return true;
        }
        return false;
    }
}
