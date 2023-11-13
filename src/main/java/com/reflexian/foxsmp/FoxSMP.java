package com.reflexian.foxsmp;

import com.reflexian.foxsmp.features.candy.GivePetCandyCommand;
import com.reflexian.foxsmp.features.candy.PetCandyItem;
import com.reflexian.foxsmp.features.pets.helpers.CombatListener;
import com.reflexian.foxsmp.features.pets.helpers.CombatTask;
import com.reflexian.foxsmp.features.pets.helpers.PetListeners;
import com.reflexian.foxsmp.features.pets.helpers.PveZoneFlag;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.inventory.InvUtils;
import com.sk89q.worldguard.WorldGuard;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class FoxSMP extends JavaPlugin implements Listener {

    @Getter private static FoxSMP instance;
    @Getter private InventoryManager inventoryManager;
    @Getter private PetCandyItem petCandyItem;
    @Getter private PveZoneFlag pveZoneFlag;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new PetListeners();
        new CombatListener();

        new CombatTask().runTaskLaterAsynchronously(this, 10L);

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        InvUtils.init();
        checkInvFile("journey.yml");

        this.petCandyItem = new PetCandyItem(getConfig().getConfigurationSection("pet-candy"));
        new GivePetCandyCommand().register();
    }

    @Override
    public void onLoad() {
        this.pveZoneFlag = new PveZoneFlag();
        WorldGuard.getInstance().getFlagRegistry().register(pveZoneFlag);
    }

    @Override
    public void onDisable() {
        for (PlayerData value : PlayerData.map.values()) {
            PlayerData.save(value);
        }
    }

    private void checkInvFile(String file) {
        File configFile = new File(getDataFolder()+ "/inventories", file);
        YamlConfiguration c;
        if (!configFile.exists()) {
            try {
                c = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("inventories/"+file), "UTF8"));
                c.save(getDataFolder() + "/inventories" + File.separator + file);
            } catch (Exception e) {
                getLogger().warning("Unable to save " +configFile.getName()+"!");
            }
            getLogger().info("Generated " + configFile.getName()+"!");
        }
    }
}

