package com.reflexian.foxsmp.utilities.data.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.reflexian.foxsmp.utilities.data.PlayerData;

import java.lang.reflect.Type;

public class UserDataSerializer implements JsonSerializer<PlayerData> {
    @Override
    public JsonElement serialize(PlayerData playerData, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", playerData.getUuid().toString());
        jsonObject.addProperty("petId", playerData.getPet().getUuid().toString());
        jsonObject.addProperty("petName", playerData.getPet().getName());
        jsonObject.addProperty("petXp", playerData.getPet().getXp());
        return jsonObject;

    }
}



