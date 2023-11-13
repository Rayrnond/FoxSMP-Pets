package com.reflexian.foxsmp.features.candy;

import com.reflexian.foxsmp.FoxSMP;
import de.tr7zw.nbtapi.NBT;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public class GivePetCandyCommand extends CommandAPICommand {

    public GivePetCandyCommand() {
        super("givepetcandy");
        withPermission("foxsmp.givepetcandy");
        withArguments(new PlayerArgument("player"), new IntegerArgument("amount", 0));
        executesPlayer((player, arguments)-> {
            FoxSMP.getInstance().getPetCandyItem().givePlayerCandy(player, (int) arguments.getOrDefault("amount", 1));
        });
    }

}
