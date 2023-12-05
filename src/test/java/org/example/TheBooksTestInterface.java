package org.example;
import org.junit.Test;
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
    @Test
    void testUpdateCurrentSetOnMonthChange();
}

