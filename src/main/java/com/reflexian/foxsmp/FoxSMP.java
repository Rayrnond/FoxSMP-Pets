package com.reflexian.foxsmp;

import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.candy.GivePetCandyCommand;
import com.reflexian.foxsmp.features.candy.PetCandyItem;
import com.reflexian.foxsmp.features.inventories.JourneyCrystalCommand;
import com.reflexian.foxsmp.features.inventories.JourneyCrystalGUI;
import com.reflexian.foxsmp.features.journeycrystal.GiveJourneyCrystalCommand;
import com.reflexian.foxsmp.features.journeycrystal.JourneyCrystalItem;
import com.reflexian.foxsmp.features.pets.helpers.CombatListener;
import com.reflexian.foxsmp.features.pets.helpers.CombatTask;
import com.reflexian.foxsmp.features.pets.helpers.PetListeners;
import com.reflexian.foxsmp.features.pets.helpers.PveZoneFlag;
import com.reflexian.foxsmp.utilities.Placeholders;
import com.reflexian.foxsmp.utilities.inventory.InvUtils;
import com.sk89q.worldguard.WorldGuard;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class FoxSMP extends JavaPlugin {

    @Getter private static FoxSMP instance;
    @Getter private InventoryManager inventoryManager;
    @Getter private PetCandyItem petCandyItem;
    @Getter private JourneyCrystalItem journeyCrystalItem;
    @Getter private PveZoneFlag pveZoneFlag;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new PetListeners();
        new CombatListener();

        new CombatTask().runTaskTimer(this, 40, 10L);

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        InvUtils.init();
        checkInvFile("journey.yml");

        this.petCandyItem = new PetCandyItem(getConfig().getConfigurationSection("pet-candy"));
        this.journeyCrystalItem = new JourneyCrystalItem(getConfig().getConfigurationSection("journey-crystal"));
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        CommandAPI.onEnable();

        new GivePetCandyCommand().register();
        new GiveJourneyCrystalCommand().register();
        new JourneyCrystalCommand().register();

        new Placeholders().register();
    }

    @Override
    public void onLoad() {
        this.pveZoneFlag = new PveZoneFlag();
        WorldGuard.getInstance().getFlagRegistry().register(pveZoneFlag);
    }

    @Override
    public void onDisable() {
        for (BalloonImpl value : BalloonImpl.balloons.values()) {
            value.kill();
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

