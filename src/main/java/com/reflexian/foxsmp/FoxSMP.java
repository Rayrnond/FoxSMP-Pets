package com.reflexian.foxsmp;

import com.reflexian.foxsmp.pets.helpers.ArmorStandHoverTask;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoxSMP extends JavaPlugin {

    @Getter
    private static FoxSMP instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        new ArmorStandHoverTask().runTaskTimer(this, 0L, 1L);

    }

    @Override
    public void onDisable() {

    }
}
