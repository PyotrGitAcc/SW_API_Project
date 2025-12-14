package org.Main.UtilityClasses;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Main.HelperClasses.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * Класс для отображения детальной информации о сущностях SWAPI.
 * Создает окна с информацией о персонажах, планетах и кораблях.
 */
public class ItemDisplayWindow {

    /**
     * Отображает окно с информацией о персонаже.
     *
     * @param person объект с данными персонажа
     */
    public static void showPersonWindow(PersonInfoDTO person) {
        showItemWindow(
                "Персонаж: " + person.name(),
                content -> addPersonDetails(content, person)
        );
    }

    /**
     * Отображает окно с информацией о планете.
     *
     * @param planet объект с данными планеты
     */
    public static void showPlanetWindow(PlanetInfoDTO planet) {
        showItemWindow(
                "Планета: " + planet.name(),
                content -> addPlanetDetails(content, planet)
        );
    }

    /**
     * Отображает окно с информацией о корабле.
     *
     * @param starship объект с данными корабля
     */
    public static void showStarshipWindow(StarshipInfoDTO starship) {
        showItemWindow(
                "Корабль: " + starship.name(),
                content -> addStarshipDetails(content, starship)
        );
    }

    /**
     * Создает общее окно для отображения информации.
     *
     * @param title заголовок окна
     * @param detailsBuilder функция для добавления деталей
     */
    private static void showItemWindow(String title, Consumer<VBox> detailsBuilder) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        VBox content = new VBox(10);
        detailsBuilder.accept(content);
        scrollPane.setContent(content);

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> window.close());

        layout.getChildren().addAll(scrollPane, closeButton);

        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Добавляет детали о персонаже в контейнер.
     *
     * @param content контейнер для добавления элементов
     * @param person данные персонажа
     */
    private static void addPersonDetails(VBox content, PersonInfoDTO person) {
        addDetail(content, "Имя:", person.name());
        addDetail(content, "Рост:", person.height() + " см");
        addDetail(content, "Вес:", person.mass() + " кг");
        addDetail(content, "Цвет волос:", person.hair_color());
        addDetail(content, "Цвет кожи:", person.skin_color());
        addDetail(content, "Цвет глаз:", person.eye_color());
        addDetail(content, "Год рождения:", person.birth_year());
        addDetail(content, "Пол:", person.gender());
        addDetail(content, "Родная планета:", person.homeworld());

        addOptionalList(content, "Фильмы:", person.films());
        addOptionalList(content, "Виды:", person.species());
        addOptionalList(content, "Транспорт:", person.vehicles());
        addOptionalList(content, "Корабли:", person.starships());
    }

    /**
     * Добавляет детали о планете в контейнер.
     *
     * @param content контейнер для добавления элементов
     * @param planet данные планеты
     */
    private static void addPlanetDetails(VBox content, PlanetInfoDTO planet) {
        addDetail(content, "Название:", planet.name());
        addDetail(content, "Диаметр:", planet.diameter() + " км");
        addDetail(content, "Период вращения:", planet.rotation_period() + " часов");
        addDetail(content, "Орбитальный период:", planet.orbital_period() + " дней");
        addDetail(content, "Гравитация:", planet.gravity());
        addDetail(content, "Население:", planet.population());
        addDetail(content, "Климат:", planet.climate());
        addDetail(content, "Рельеф:", planet.terrain());
        addDetail(content, "Поверхностная вода:", planet.surface_water() + "%");

        addOptionalList(content, "Жители:", planet.residents());
        addOptionalList(content, "Фильмы:", planet.films());
    }

    /**
     * Добавляет детали о корабле в контейнер.
     *
     * @param content контейнер для добавления элементов
     * @param starship данные корабля
     */
    private static void addStarshipDetails(VBox content, StarshipInfoDTO starship) {
        addDetail(content, "Название:", starship.name());
        addDetail(content, "Модель:", starship.model());
        addDetail(content, "Производитель:", starship.manufacturer());
        addDetail(content, "Стоимость:", starship.cost_in_credits() + " кредитов");
        addDetail(content, "Длина:", starship.length() + " метров");
        addDetail(content, "Макс. скорость:", starship.max_atmosphering_speed());
        addDetail(content, "Экипаж:", starship.crew());
        addDetail(content, "Пассажиры:", starship.passengers());
        addDetail(content, "Грузоподъемность:", starship.cargo_capacity() + " кг");
        addDetail(content, "Расходники:", starship.consumables());
        addDetail(content, "Рейтинг гипердрайва:", starship.hyperdrive_rating());
        addDetail(content, "MGLT:", starship.MGLT());
        addDetail(content, "Класс корабля:", starship.starship_class());

        addOptionalList(content, "Пилоты:", starship.pilots());
        addOptionalList(content, "Фильмы:", starship.films());
    }

    /**
     * Добавляет строку с деталью в контейнер.
     *
     * @param container контейнер для добавления
     * @param label метка детали
     * @param value значение детали
     */
    private static void addDetail(VBox container, String label, String value) {
        HBox detail = new HBox(10);
        detail.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");

        Label valueLabel = new Label(value != null ? value : "Н/Д");

        detail.getChildren().addAll(titleLabel, valueLabel);
        container.getChildren().add(detail);
    }

    /**
     * Добавляет список, если он не пустой.
     *
     * @param container контейнер для добавления
     * @param label метка списка
     * @param items элементы списка
     */
    private static void addOptionalList(VBox container, String label, List<String> items) {
        if (items != null && !items.isEmpty()) {
            addListDetail(container, label, items);
        }
    }

    /**
     * Добавляет список элементов в контейнер.
     *
     * @param container контейнер для добавления
     * @param label метка списка
     * @param items элементы списка
     */
    private static void addListDetail(VBox container, String label, List<String> items) {
        VBox detail = new VBox(5);

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox listBox = new VBox(2);
        for (String item : items) {
            Label itemLabel = new Label("• " + item);
            listBox.getChildren().add(itemLabel);
        }

        detail.getChildren().addAll(titleLabel, listBox);
        container.getChildren().add(detail);
    }
}