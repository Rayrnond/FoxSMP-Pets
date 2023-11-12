package com.reflexian.foxsmp.pets.helpers;

import com.google.gson.annotations.Expose;
import com.reflexian.foxsmp.utilities.balloons.BalloonBlueprint;
import com.reflexian.foxsmp.utilities.balloons.Skin;
import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.UUID;

@Getter@Setter
public class BalloonBlueprintImpl implements BalloonBlueprint {

    public String id = UUID.randomUUID().toString();
    @Expose public Skin skin= Skin.DEFAULT_SKIN;
    @Expose public int trailColor = Color.darkGray.getRGB();

    @Override
    public Skin getSkin() {
        return skin;
    }

    public BalloonBlueprintImpl setTrailColor(Color trailColor) {
        this.trailColor = trailColor.getRGB();
        return this;
    }

    public BalloonBlueprintImpl setSkin(Skin skin) {
        this.skin = skin;
        return this;
    }

    @Override
    public Color getTrailColor() {
        return new Color(trailColor);
    }

    @Override
    public ItemStack getBalloonItem() {
        return new ItemBuilder(Material.AIR).setSkull(skin).build();
    }
}