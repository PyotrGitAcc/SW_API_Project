package org.Main.HelperClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** DTO для работы с персонажами из базы данных */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonInfoDTO(
        String name,
        String height,
        String mass,
        String hair_color,
        String skin_color,
        String eye_color,
        String birth_year,
        String gender,
        String homeworld,
        List<String> films,
        List<String> species,
        List<String> vehicles,
        List<String> starships,
        String created,
        String edited,
        String url
) {}