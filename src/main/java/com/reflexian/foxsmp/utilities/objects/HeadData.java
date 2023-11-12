package com.reflexian.foxsmp.utilities.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter@AllArgsConstructor@Builder
public class HeadData { // represents a head texture // todo support for config deserialization

    private final String name;
    private final String texture;
}
