package com.reflexian.foxsmp;

import com.reflexian.foxsmp.pets.helpers.ArmorStandHoverTask;
import com.reflexian.foxsmp.pets.helpers.BalloonBlueprintImpl;
import com.reflexian.foxsmp.pets.helpers.BalloonImpl;
import com.reflexian.foxsmp.utilities.balloons.Skin;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

public final class FoxSMP extends JavaPlugin implements Listener {

    @Getter
    private static FoxSMP instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
//        this.getServer().getPluginManager().registerEvents(this, this);
        new ArmorStandHoverTask().runTaskTimer(this, 0L, 1L);

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BalloonImpl.setBalloon(event.getPlayer(), new BalloonBlueprintImpl().setSkin(Skin.DEFAULT_SKIN).setTrailColor(Color.darkGray));
    }
}

