package org.Main;
import org.Main.HelperClasses.*;
import org.Main.UtilityClasses.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Главный класс приложения для работы с Star Wars API.
 * JavaFX приложение для поиска и отображения данных о персонажах, планетах и кораблях.
 */
public class Main extends Application {
    /** Логгер для записи событий приложения */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /** Список для хранения результатов поиска людей */
    public ObservableList<ItemHelperEntry> people;
    /** Список для хранения результатов поиска планет */
    public ObservableList<ItemHelperEntry> planets;
    /** Список для хранения результатов поиска кораблей */
    public ObservableList<ItemHelperEntry> starships;

    /** Элементы UI для отображения списков */
    private ListView<ItemHelperEntry> peopleListView, planetsListView, starshipsListView;

    /** Поле для ввода поискового запроса */
    private TextField inputSearchField;

    /** Кнопки управления */
    private Button openSelectedButton, showAllButton;

    /**
     * Основной метод запуска JavaFX приложения.
     * Инициализирует интерфейс и настраивает обработчики событий.
     *
     * @param primaryStage главное окно приложения
     */
    @Override
    public void start(Stage primaryStage) {
        logger.info("Запуск SW API Client");

        // Обработчик неожиданных ошибок в JavaFX потоке
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Необработанная ошибка в JavaFX потоке: {}", throwable.getMessage());
            JavaFXWindowsUtility.showErrorAlert("Ошибка приложения", "Произошла ошибка: " + throwable.getMessage());
        });

        // Инициализация списков
        people = FXCollections.observableArrayList();
        planets = FXCollections.observableArrayList();
        starships = FXCollections.observableArrayList();

        // Настройка ListView для персонажей
        peopleListView = new ListView<>(people);
        peopleListView.setPrefHeight(300);
        peopleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Настройка ListView для планет
        planetsListView = new ListView<>(planets);
        planetsListView.setPrefHeight(300);
        planetsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Настройка ListView для кораблей
        starshipsListView = new ListView<>(starships);
        starshipsListView.setPrefHeight(300);
        starshipsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Настройка поля поиска
        inputSearchField = new TextField();
        inputSearchField.setPromptText("Введите запрос для поиска");
        inputSearchField.setOnAction(e -> {
            try {
                searchForItems();
            } catch (IOException ex) {
                logger.error("Ошибка при поиске: {}", ex.getMessage());
                JavaFXWindowsUtility.showErrorAlert("Ошибка поиска", "Не удалось выполнить поиск: " + ex.getMessage());
            } catch (Exception ex) {
                logger.error("Неожиданная ошибка при поиске: {}", ex.getMessage());
                JavaFXWindowsUtility.showErrorAlert("Ошибка поиска", "Произошла неожиданная ошибка");
            }
        });

        // Кнопка открытия выбранных элементов
        openSelectedButton = new Button("Открыть выбранное");
        openSelectedButton.setOnAction(e -> openSelected());

        // Кнопка показа всех элементов
        showAllButton = new Button("Показать все записи");
        showAllButton.setOnAction(e -> {
            try {
                JavaFXWindowsUtility.showAll("DataQueries/PeopleQueries.json", people);
                JavaFXWindowsUtility.showAll("DataQueries/PlanetQueries.json", planets);
                JavaFXWindowsUtility.showAll("DataQueries/StarshipQueries.json", starships);
            } catch (IOException ex) {
                logger.error("Ошибка при загрузке всех элементов: {}", ex.getMessage());
                JavaFXWindowsUtility.showErrorAlert("Ошибка загрузки", "Не удалось загрузить элементы: " + ex.getMessage());
            } catch (Exception ex) {
                logger.error("Неожиданная ошибка: {}", ex.getMessage());
                JavaFXWindowsUtility.showErrorAlert("Ошибка загрузки", "Произошла неожиданная ошибка");
            }
        });

        // Создание макета интерфейса
        HBox inputBox = new HBox(10, inputSearchField);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        HBox buttonBox = new HBox(10, openSelectedButton, showAllButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Список персонажей:"),
                peopleListView,
                new Label("Список планет:"),
                planetsListView,
                new Label("Список кораблей:"),
                starshipsListView,
                new Label("Поиск запроса:"),
                inputBox,
                buttonBox
        );

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Star Wars API список");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * Выполняет поиск по всем категориям.
     * Очищает старые результаты и показывает новые.
     *
     * @throws IOException если не удалось прочитать JSON файлы
     */
    private void searchForItems() throws IOException {
        people.clear();
        planets.clear();
        starships.clear();

        String searchInput = inputSearchField.getText().trim().toLowerCase();
        logger.info("Поиск элементов по запросу: {}", searchInput);

        if (searchInput.isEmpty()) {
            return; // Пустой запрос
        }

        // Поиск персонажей (максимум 5 результатов)
        List<ItemHelperEntry> closestPeopleEntries = SearchTools.searchClosestEntries("DataQueries/PeopleQueries.json", searchInput);
        if (closestPeopleEntries.size() > 5) {
            closestPeopleEntries.subList(5, closestPeopleEntries.size()).clear();
        }
        people.addAll(closestPeopleEntries);

        // Поиск планет (максимум 5 результатов)
        List<ItemHelperEntry> closestPlanetsEntries = SearchTools.searchClosestEntries("DataQueries/PlanetQueries.json", searchInput);
        if (closestPlanetsEntries.size() > 5) {
            closestPlanetsEntries.subList(5, closestPlanetsEntries.size()).clear();
        }
        planets.addAll(closestPlanetsEntries);

        // Поиск кораблей (максимум 5 результатов)
        List<ItemHelperEntry> closestStarshipsEntries = SearchTools.searchClosestEntries("DataQueries/StarshipQueries.json", searchInput);
        if (closestStarshipsEntries.size() > 5) {
            closestStarshipsEntries.subList(5, closestStarshipsEntries.size()).clear();
        }
        starships.addAll(closestStarshipsEntries);
    }

    /**
     * Открывает детальную информацию о выбранных элементах.
     * Загружает данные из SWAPI и отображает в отдельных окнах.
     */
    private void openSelected() {
        SwapiClient swAPI = new SwapiClient();

        // Получение выбранных элементов
        ObservableList<ItemHelperEntry> selectedPeople = peopleListView.getSelectionModel().getSelectedItems();
        int[] peopleIdArray = selectedPeople.stream().mapToInt(ItemHelperEntry::getId).toArray();

        ObservableList<ItemHelperEntry> selectedPlanets = planetsListView.getSelectionModel().getSelectedItems();
        int[] planetsIdArray = selectedPlanets.stream().mapToInt(ItemHelperEntry::getId).toArray();

        ObservableList<ItemHelperEntry> selectedStarships = starshipsListView.getSelectionModel().getSelectedItems();
        int[] starshipsIdArray = selectedStarships.stream().mapToInt(ItemHelperEntry::getId).toArray();

        try {
            // Открытие информации о персонажах
            for (int id : peopleIdArray) {
                try {
                    PersonInfoDTO personInfo = swAPI.getPerson(id);
                    logger.info("Открыт персонаж с id: {}", id);
                    ItemDisplayWindow.showPersonWindow(personInfo);
                } catch (SwapiClient.SwapiException e) {
                    logger.error("Ошибка загрузки персонажа ID {}: {}", id, e.getMessage());
                }
            }

            // Открытие информации о планетах
            for (int id : planetsIdArray) {
                try {
                    PlanetInfoDTO planetInfo = swAPI.getPlanet(id);
                    logger.info("Открыта планета с id: {}", id);
                    ItemDisplayWindow.showPlanetWindow(planetInfo);
                } catch (SwapiClient.SwapiException e) {
                    logger.error("Ошибка загрузки планеты ID {}: {}", id, e.getMessage());
                }
            }

            // Открытие информации о кораблях
            for (int id : starshipsIdArray) {
                try {
                    StarshipInfoDTO shipInfo = swAPI.getStarship(id);
                    logger.info("Открыт корабль с id: {}", id);
                    ItemDisplayWindow.showStarshipWindow(shipInfo);
                } catch (SwapiClient.SwapiException e) {
                    logger.error("Ошибка загрузки корабля ID {}: {}", id, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка в openSelected: {}", e.getMessage());
        }
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    static void main(String[] args) {
        launch(args);
    }
}