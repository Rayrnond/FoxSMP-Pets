package com.reflexian.foxsmp.utilities.data.helpers;

import com.google.gson.*;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.features.pets.list.*;
import com.reflexian.foxsmp.utilities.data.PlayerData;

import java.lang.reflect.Type;
import java.util.UUID;

public class UserDataDeserializer implements JsonDeserializer<PlayerData> {
    @Override
    public PlayerData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();
        PlayerData playerData = new PlayerData(UUID.fromString(object.get("uuid").getAsString()));

        System.out.println("a1");
        if (object.has("petName")) {
            SMPPet pet = switch (object.get("petName").getAsString()) {
                default -> new NonePet();
                case "avalanche_artisan" -> new AvalancheArtisanPet(playerData);
                case "polar_explorer" -> new PolarExplorerPet(playerData);
                case "northern_nomad" -> new NorthernNomadPet(playerData);
                case "glacial_guardian" -> new GlacialGuardianPet(playerData);
            };
            pet.setXp(object.get("petXp").getAsDouble());
            pet.setUuid(UUID.fromString(object.get("petId").getAsString()));
            pet.setBalloon(new BalloonImpl(playerData.getUuid(), pet.getSkin()));
            pet.getBalloon().startTask();
            playerData.setPet(pet);
        } else {
            playerData.setPet(null);
        }
        return playerData;
    }
}



