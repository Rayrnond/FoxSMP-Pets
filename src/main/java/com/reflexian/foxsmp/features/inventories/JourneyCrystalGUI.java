package com.reflexian.foxsmp.features.inventories;

import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.features.pets.list.AvalancheArtisanPet;
import com.reflexian.foxsmp.features.pets.list.GlacialGuardianPet;
import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.features.pets.list.PolarExplorerPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.inventory.ClickAction;
import com.reflexian.foxsmp.utilities.inventory.InvUtils;
import com.reflexian.foxsmp.utilities.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JourneyCrystalGUI implements Inventory {
    @Override
    public void init(Player player) {
        final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
        if (playerData != null && playerData.getPet() != null) {
            player.sendMessage("§cYou already have a pet! Use a Journey Crystal to remove it.");
            player.closeInventory();
            return;
        } else if (playerData == null) {
            player.sendMessage("§cAn error occurred. Please try again later.");
            player.closeInventory();
            return;
        }
        InvUtils.showInventory(player,"journey", true,

                new ClickAction("glacialguardian", (p, clickAction) -> {
                    setPet(playerData, new GlacialGuardianPet(playerData));
                    player.closeInventory();
                }),
                new ClickAction("avalancheartisan", (p, clickAction) -> {
                    setPet(playerData, new AvalancheArtisanPet(playerData));
                    player.closeInventory();
                }),
                new ClickAction("northernnomad", (p, clickAction) -> {
                    setPet(playerData, new NorthernNomadPet(playerData));
                    player.closeInventory();
                }),
                new ClickAction("polarexplorer", (p, clickAction) -> {
                    setPet(playerData, new PolarExplorerPet(playerData));
                    player.closeInventory();
                })
        );
    }

    private void setPet(PlayerData playerData, SMPPet pet) {
        if (playerData.hasPet()) {
            playerData.getPet().getBalloon().kill();
        }
        pet.setBalloon(new BalloonImpl(playerData.getUuid(), pet.getSkin()));
        playerData.setPet(pet);
        playerData.getPet().getBalloon().startTask();
        PlayerData.save(playerData);
        Bukkit.getPlayer(playerData.getUuid()).sendMessage("§aYou have successfully set your pet to §e" + pet.getName() + "§a!");
    }
}
