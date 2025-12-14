package org.Main.UtilityClasses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.Main.HelperClasses.ItemHelperEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Инструменты для отображения различных элементов интерфейса в программе.
 */
public class JavaFXWindowsUtility {
    /** Логгер для записи событий клиента */
    private static final Logger logger = LogManager.getLogger(JavaFXWindowsUtility.class);

    /**
     * Загружает и отображает все записи из JSON файла.
     *
     * @param jsonFilePath путь к JSON файлу
     * @param targetObservableList список для заполнения результатами
     * @throws IOException если файл не найден или поврежден
     */
    public static void showAll(String jsonFilePath, ObservableList<ItemHelperEntry> targetObservableList) throws IOException {
        targetObservableList.clear();
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStream = JavaFXWindowsUtility.class.getClassLoader().getResourceAsStream(jsonFilePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Файл не найден: " + jsonFilePath);
            }

            List<Map<String, Object>> objects = objectMapper.readValue(inputStream, new TypeReference<>() {});

            for (Map<String, Object> object : objects) {
                try {
                    // Обработка ID
                    Object idObj = object.get("id");
                    int id;
                    if (idObj instanceof Number) {
                        id = ((Number) idObj).intValue();
                    } else if (idObj instanceof String) {
                        id = Integer.parseInt((String) idObj);
                    } else {
                        throw new IllegalArgumentException("Неверный тип ID: " +
                                (idObj != null ? idObj.getClass().getName() : "null"));
                    }

                    // Обработка имен
                    List<String> names = objectMapper.convertValue(object.get("strings"), new TypeReference<>() {});
                    String name = String.join(", ", names);
                    targetObservableList.add(new ItemHelperEntry(id, name));

                } catch (Exception e) {
                    throw new IOException("Ошибка обработки элемента с id: " + object.get("id"), e);
                }
            }
        }
    }

    /**
     * Показывает диалоговое окно с ошибкой.
     *
     * @param title заголовок окна
     * @param message текст сообщения об ошибке
     */
    public static void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                logger.error("Не удалось показать окно ошибки: {}", e.getMessage());
            }
        });
    }
}
