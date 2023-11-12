package com.reflexian.foxsmp;

import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoxSMP extends JavaPlugin implements Listener {

    @Getter
    private static FoxSMP instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {

    }

    // todo remove after testing
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BalloonImpl.setBalloon(event.getPlayer(), Skin.DEFAULT_SKIN);
    }
}

