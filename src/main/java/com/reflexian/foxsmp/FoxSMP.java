package com.reflexian.foxsmp;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoxSMP extends JavaPlugin {

    @Getter
    private static FoxSMP instance;

    @Override
    public void onEnable() {
        instance = this;


    }

    @Override
    public void onDisable() {

    }
}
