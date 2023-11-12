package com.reflexian.foxsmp.utilities.data;

import com.reflexian.foxsmp.features.pets.SMPPet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter@Setter@Builder
public class PlayerData {

    private UUID uuid;
    private SMPPet pet;
}
