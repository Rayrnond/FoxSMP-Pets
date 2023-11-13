package com.reflexian.foxsmp.utilities.inventory;

import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class InvLang {

    /**
     *
     * path:
     *  type:
     *  amount:
     *  data:
     *  name:
     *  lore:
     *  - ""
     *  - ""
     *
     */

//    public static ItemStack itemStackInvs(YamlConfiguration c, String s,Player player) {
//        return itemStackInvs(c,s,player);
//    }

    public static ItemStack itemStackInvs(YamlConfiguration c, String s, Player player) {
        ItemBuilder builder = new ItemBuilder(Material.valueOf(c.getString(s + ".material").toUpperCase()));

        if (c.contains(s + ".itemsadderID")) {
            String id = c.getString(s + ".itemsadderID");
            if (id != null && !id.isEmpty()) {
                CustomStack stack = CustomStack.getInstance(id);

                if (stack == null) {
                    throw new RuntimeException("Failed to serialize item with ID: " + s + ". Could not find ItemsAdder item with id " + id);
                }

                builder = new ItemBuilder(stack.getItemStack());
            }
        }

        if (c.contains(s + ".data")) builder.setDurability((byte) c.getInt(s + ".data"));
        if (c.contains(s + ".amount")) builder.setAmount(c.getInt(s + ".amount"));
        if (c.contains(s + ".displayname")) builder.setName(format(c.getString(s + ".displayname")));
        if (c.contains(s + ".lore")) {
            List<String> lore = format(c.getStringList(s + ".lore"));
            for (String s1 : lore) {

                builder.addLoreLine(s1);
            }
        }
        return builder.build();
    }



    public static String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private List<String> format(List<String> lore) {
        List<String> l = new ArrayList<>();
        for (String s : lore) {
            l.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return l;
    }

}
