package com.reflexian.foxsmp;

import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.utilities.inventory.InvUtils;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class FoxSMP extends JavaPlugin implements Listener {

    @Getter private static FoxSMP instance;
    @Getter private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
//        this.getServer().getPluginManager().registerEvents(this, this);

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        InvUtils.init();
        checkInvFile("journey.yml");
    }

    @Override
    public void onDisable() {

    }

    // todo remove after testing
//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        BalloonImpl.setBalloon(event.getPlayer(), Skin.DEFAULT_SKIN);
//    }

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

