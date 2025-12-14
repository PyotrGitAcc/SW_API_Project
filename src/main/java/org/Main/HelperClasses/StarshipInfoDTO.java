package org.Main.HelperClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** DTO для работы с кораблями из базы данных */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StarshipInfoDTO(
        String name,
        String model,
        String manufacturer,
        String cost_in_credits,
        String length,
        String max_atmosphering_speed,
        String crew,
        String passengers,
        String cargo_capacity,
        String consumables,
        String hyperdrive_rating,
        String MGLT,
        String starship_class,
        List<String> pilots,
        List<String> films,
        String created,
        String edited,
        String url
) {}