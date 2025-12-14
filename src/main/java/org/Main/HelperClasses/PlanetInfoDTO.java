package org.Main.HelperClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** DTO для работы с планетами из базы данных */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlanetInfoDTO(
        String name,
        String diameter,
        String rotation_period,
        String orbital_period,
        String gravity,
        String population,
        String climate,
        String terrain,
        String surface_water,
        List<String> residents,
        List<String> films,
        String created,
        String edited,
        String url
) {}