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
        SMPPet pet = null;
        switch (object.get("petName").getAsString()) {
            default:
                pet = new NonePet();
                break;
            case "avalanche_artisan":
                 pet = new AvalancheArtisanPet();
                break;
            case "polar_explorer":
                pet = new PolarExplorerPet();
                break;
            case "northern_nomad":
                pet = new NorthernNomadPet();
                break;
            case "glacial_guardian":
                pet = new GlacialGuardianPet();
                break;
        }
        pet.setXp(object.get("petXp").getAsDouble());
        pet.setUuid(UUID.fromString(object.get("petId").getAsString()));
        pet.setBalloon(new BalloonImpl(playerData.getUuid(), pet.getSkin()));
        playerData.setPet(pet);
        return playerData;
    }
}



