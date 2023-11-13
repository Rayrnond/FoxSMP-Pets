package com.reflexian.foxsmp.features.pets;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.utilities.objects.HeadData;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

@Getter
public abstract class SMPPet {

    private transient BalloonImpl balloon; // transient so it doesn't get saved

    private UUID uuid;
    private UUID owner;
    private double xp;


    public abstract String getName(); // used for config and saving

    public Skin getSkin() {
        ConfigurationSection section = FoxSMP.getInstance().getConfig().getConfigurationSection("pets." + getName());
        if (section == null) return Skin.DEFAULT_SKIN;

        return Skin.builder()
                .texture(section.getString("texture", Skin.DEFAULT_SKIN.getTexture()))
                .signature(section.getString("signature", Skin.DEFAULT_SKIN.getSignature()))
                .build();
    }

}
