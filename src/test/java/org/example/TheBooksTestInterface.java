package org.example;

/**
 * Интерфейс для тестирования функциональности класса {@link TheBooks}.
 */
public interface TheBooksTestInterface {

    /**
     * Подготовка данных перед каждым тестом.
     */
    void setUp();

    /**
     * Тестирование обновления текущей десятки при смене месяца.
     */
    void testUpdateCurrentSetOnMonthChange();
}
