package org.Main.UtilityClasses;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpConnectTimeoutException;
import java.time.Duration;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.Main.HelperClasses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Клиент для работы с Star Wars API (SWAPI).
 * Предоставляет методы для получения данных о персонажах, планетах и кораблях.
 */
public class SwapiClient {
    /** Логгер для записи событий клиента */
    private static final Logger logger = LogManager.getLogger(SwapiClient.class);

    /** Объект для преобразования JSON в Java объекты */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** HTTP клиент для выполнения запросов */
    private final HttpClient httpClient;

    /** Базовый URL SWAPI */
    private static final String BASE_URL = "https://swapi.dev/api/";

    /**
     * Базовое исключение для ошибок SWAPI.
     */
    public static class SwapiException extends Exception {
        public SwapiException(String message) { super(message); }
        public SwapiException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Исключение при отсутствии ресурса (HTTP 404).
     */
    public static class SwapiNotFoundException extends SwapiException {
        public SwapiNotFoundException(String message) { super(message); }
    }

    /**
     * Исключение при ошибках сервера (HTTP 5xx).
     */
    public static class SwapiServerException extends SwapiException {
        public SwapiServerException(String message) { super(message); }
    }

    /**
     * Создает клиент с настройками HTTP.
     */
    public SwapiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Получает информацию о персонаже по ID.
     *
     * @param id идентификатор персонажа
     * @return объект с данными персонажа
     * @throws SwapiException при ошибке запроса или парсинга
     */
    public PersonInfoDTO getPerson(int id) throws SwapiException {
        logger.info("Получение данных персонажа с ID: {}", id);
        String json = fetchJson("people/" + id);
        try {
            return objectMapper.readValue(json, PersonInfoDTO.class);
        } catch (JsonProcessingException e) {
            throw new SwapiException("Ошибка парсинга данных персонажа с ID: " + id, e);
        }
    }

    /**
     * Получает информацию о планете по ID.
     *
     * @param id идентификатор планеты
     * @return объект с данными планеты
     * @throws SwapiException при ошибке запроса или парсинга
     */
    public PlanetInfoDTO getPlanet(int id) throws SwapiException {
        logger.info("Получение данных планеты с ID: {}", id);
        String json = fetchJson("planets/" + id);
        try {
            return objectMapper.readValue(json, PlanetInfoDTO.class);
        } catch (JsonProcessingException e) {
            throw new SwapiException("Ошибка парсинга данных планеты с ID: " + id, e);
        }
    }

    /**
     * Получает информацию о корабле по ID.
     *
     * @param id идентификатор корабля
     * @return объект с данными корабля
     * @throws SwapiException при ошибке запроса или парсинга
     */
    public StarshipInfoDTO getStarship(int id) throws SwapiException {
        logger.info("Получение данных корабля с ID: {}", id);
        String json = fetchJson("starships/" + id);
        try {
            return objectMapper.readValue(json, StarshipInfoDTO.class);
        } catch (JsonProcessingException e) {
            throw new SwapiException("Ошибка парсинга данных корабля с ID: " + id, e);
        }
    }

    /**
     * Выполняет HTTP запрос к SWAPI и возвращает JSON ответ.
     *
     * @param endpoint конечная точка API
     * @return JSON строка с данными
     * @throws SwapiException при ошибках сети или HTTP
     */
    private String fetchJson(String endpoint) throws SwapiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return response.body();
            } else if (statusCode == 404) {
                throw new SwapiNotFoundException("Ресурс не найден: " + endpoint);
            } else if (statusCode >= 500) {
                throw new SwapiServerException("Ошибка сервера (" + statusCode +
                        ") для: " + endpoint);
            } else {
                throw new SwapiException("HTTP ошибка (" + statusCode +
                        ") для: " + endpoint);
            }

        } catch (HttpConnectTimeoutException e) {
            throw new SwapiException("Таймаут подключения для: " + endpoint + ". Проверьте подключение к интернету", e);
        } catch (IOException e) {
            throw new SwapiException("Ошибка ввода-вывода для: " + endpoint + ". Проверьте состояние API на https://swapi.dev", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SwapiException("Запрос прерван для: " + endpoint, e);
        }
    }
}