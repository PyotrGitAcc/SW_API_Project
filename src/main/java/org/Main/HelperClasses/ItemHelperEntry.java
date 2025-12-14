package org.Main.HelperClasses;

public class ItemHelperEntry implements Comparable<ItemHelperEntry> {
    /** ID сущности в SWAPI */
    private int id;

    /** Имя сущности для отображения */
    private String name;

    /** Расстояние Левенштейна для сортировки по релевантности */
    private int nameDistance;

    /**
     * Создает запись без расстояния (для отображения всех элементов).
     *
     * @param id ID сущности
     * @param name имя сущности
     */
    public ItemHelperEntry(int id, String name) {
        this.id = id;
        this.name = name;
        this.nameDistance = Integer.MAX_VALUE;
    }

    /**
     * Создает запись с расстоянием (для результатов поиска).
     *
     * @param id ID сущности
     * @param name имя сущности
     * @param nameDistance расстояние Левенштейна для сортировки
     */
    public ItemHelperEntry(int id, String name, int nameDistance) {
        this.id = id;
        this.name = name;
        this.nameDistance = nameDistance;
    }

    /**
     * Сравнивает записи по расстоянию для сортировки.
     * Меньшее расстояние = выше релевантность.
     *
     * @param other другая запись для сравнения
     * @return результат сравнения расстояний
     */
    @Override
    public int compareTo(ItemHelperEntry other) {
        return Integer.compare(this.nameDistance, other.nameDistance);
    }

    /**
     * Возвращает ID сущности.
     *
     * @return ID
     */
    public int getId() { return id; }

    /**
     * Возвращает имя сущности.
     *
     * @return имя
     */
    public String getName() { return name; }

    /**
     * Возвращает расстояние Левенштейна.
     *
     * @return расстояние
     */
    public int getNameDistance() { return nameDistance; }

    /**
     * Форматирует запись для отображения в списке.
     *
     * @return строка в формате "[ID]: Имя"
     */
    @Override
    public String toString() {
        return "[" + id + "]: " + name;
    }
}