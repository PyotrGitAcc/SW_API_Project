package org.Main.UtilityClasses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.debatty.java.stringsimilarity.Levenshtein;
import org.Main.HelperClasses.ItemHelperEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Инструменты для поиска объектов в json файлах.
 * Использует записи из src/main/resources/DataQueries для поиска.
 */
public class SearchTools {

    /**
     * Ищет ближайшие совпадения в JSON файле.
     * Использует расстояние Левенштейна для нечеткого поиска.
     *
     * @param jsonFilePath путь к JSON файлу
     * @param searchInput поисковый запрос
     * @return отсортированный список ближайших совпадений
     * @throws IOException если файл не найден или поврежден
     */
    public static List<ItemHelperEntry> searchClosestEntries(String jsonFilePath, String searchInput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Levenshtein lev = new Levenshtein();

        try (InputStream inputStream = SearchTools.class.getClassLoader().getResourceAsStream(jsonFilePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Файл не найден: " + jsonFilePath);
            }

            List<Map<String, Object>> objects = objectMapper.readValue(inputStream, new TypeReference<>() {});
            List<ItemHelperEntry> entriesResults = new ArrayList<>();

            for (Map<String, Object> object : objects) {
                try {
                    // Извлечение ID
                    Object idObj = object.get("id");
                    int id;
                    if (idObj instanceof Number) {
                        id = ((Number) idObj).intValue();
                    } else if (idObj instanceof String) {
                        try {
                            id = Integer.parseInt((String) idObj);
                        } catch (NumberFormatException e) {
                            continue; // Пропуск некорректного ID
                        }
                    } else {
                        continue; // Пропуск неверного типа ID
                    }

                    // Извлечение списка имен
                    Object namesObj = object.get("strings");
                    List<String> names;
                    if (namesObj == null) {
                        names = Collections.emptyList();
                    } else {
                        try {
                            names = objectMapper.convertValue(namesObj, new TypeReference<>() {});
                        } catch (Exception e) {
                            names = Collections.emptyList();
                        }
                    }

                    // Вычисление минимального расстояния Левенштейна
                    int lowestNameDistance = Integer.MAX_VALUE;
                    for (String str : names) {
                        if (str != null) {
                            int nameDistance = (int) lev.distance(str.toLowerCase(), searchInput);
                            if (nameDistance < lowestNameDistance) {
                                lowestNameDistance = nameDistance;
                            }
                        }
                    }

                    if (lowestNameDistance == Integer.MAX_VALUE) {
                        continue; // Нет валидных имен
                    }

                    // Создание записи результата
                    String name = names.isEmpty() ? "Без имени" : String.join(", ", names);
                    entriesResults.add(new ItemHelperEntry(id, name, lowestNameDistance));

                } catch (Exception e) {
                    // Пропуск проблемных записей
                }
            }

            Collections.sort(entriesResults);
            return entriesResults;
        }
    }
}
