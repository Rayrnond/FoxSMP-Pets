package com.reflexian.foxsmp.features.pets;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.pets.list.NonePet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.objects.HeadData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.UUID;

@Getter@Setter
public abstract class SMPPet {

    private transient BalloonImpl balloon; // transient so it doesn't get saved

    private UUID uuid=UUID.randomUUID();
    private UUID owner;
    private double xp=0;

    private transient int level;

    public SMPPet(){
        this.uuid=UUID.randomUUID();
    }
    public SMPPet(PlayerData playerData) {
        this.uuid=UUID.randomUUID();
        this.owner = playerData.getUuid();
        this.xp = 0;
    }

    public abstract String getName(); // used for config and saving

    public Skin getSkin() {
        ConfigurationSection section = FoxSMP.getInstance().getConfig().getConfigurationSection("pets." + getName());
        if (section == null) return Skin.DEFAULT_SKIN;

        return Skin.builder()
                .texture(section.getString("texture", Skin.DEFAULT_SKIN.getTexture()))
                .signature(section.getString("signature", Skin.DEFAULT_SKIN.getSignature()))
                .build();
    }

    public void setXp(double xp) {
        this.xp = xp;
        int formula = FoxSMP.getInstance().getConfig().getInt("xp-per-level",200);
        this.level = (int) Math.floor(xp/formula);
        if (level>100) level=100;
    }
}
