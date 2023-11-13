package com.reflexian.foxsmp.utilities.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.utilities.data.helpers.UserDataDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Getter@Setter
public class PlayerData {

    public static final Map<UUID,PlayerData> map = new java.util.concurrent.ConcurrentHashMap<>();

    private final UUID uuid;
    private SMPPet pet=null;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        map.putIfAbsent(uuid, this);
    }

    public boolean hasPet() {
        return pet!=null;
    }

    @SneakyThrows
    public static void save(PlayerData player) {
        File dir = new File(getDataFolder()+"/data/" + player.getUuid().toString());
        if (!dir.exists()) dir.mkdirs();
        File file = new File(getDataFolder()+"/data/" + player.getUuid().toString() + "/user.json");
        file.delete();
        file.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(PlayerData.class, new UserDataDeserializer()).create();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(player, writer);
        }
    }
    @SneakyThrows
    public static PlayerData load(UUID uuid) {
        PlayerData playerData = new PlayerData(uuid);
        File dir = new File(getDataFolder()+"/data/" + uuid.toString());
        if (!dir.exists()) dir.mkdirs();
        File file = new File(getDataFolder()+"/data/" + uuid.toString() + "/user.json");
        if (!file.exists()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player!=null) {
                playerData.setPet(null);
            }
            save(playerData);
            return playerData;
        }
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(PlayerData.class, new UserDataDeserializer()).create();
            Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
            return gson.fromJson(reader, PlayerData.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return playerData;
    }

    private static String getDataFolder() {
        return FoxSMP.getInstance().getDataFolder()+"";
    }


}
