package com.reflexian.foxsmp.features.pets;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public class PetCommand extends CommandAPICommand {

    public PetCommand() {
        super("pet");
        withPermission("foxsmp.seepet");
        executesPlayer((player, arguments)-> {
            PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
            if (playerData == null  || !playerData.hasPet()) {
                player.sendMessage("§cYou do not have a pet.");
                return;
            }
            player.sendMessage("§e----------------------");
            player.sendMessage("§ePet Name: §f" + playerData.getPet().getName().replace("_", " "));
            player.sendMessage("§ePet Level: §f" + playerData.getPet().getLevel());
            player.sendMessage("§ePet XP: §f" + playerData.getPet().getXp());
            player.sendMessage("§e----------------------");
        });
    }

}
