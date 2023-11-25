package com.reflexian.foxsmp.features.candy;

import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import nl.marido.deluxecombat.events.CombatStateChangeEvent;

import java.util.Optional;

public class HidePetCommand extends CommandAPICommand {

    public HidePetCommand() {
        super("hidepet");
        withOptionalArguments(new BooleanArgument("hide"));
        executesPlayer((player, arguments)-> {
            PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(),null);
            if (playerData==null || !playerData.hasPet()) return;

            boolean hide = (boolean) arguments.getOrDefault("hide", true);

            if (hide) {
                if (playerData.getPet().getBalloon().getTask().isHidden()) return; // pet is HIDDEN

                if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                    pet.updateSpeed(player, true);
                }
                playerData.getPet().getBalloon().getTask().toggleAllHide();
                player.sendMessage("§aYour pet is now hidden!");
            } else {
                if (!playerData.getPet().getBalloon().getTask().isHidden()) return;
                if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                    pet.updateSpeed(player, false);
                }
                playerData.getPet().getBalloon().getTask().toggleAllHide();
                player.sendMessage("§aYour pet is now visible!");
            }
        });
    }

}
