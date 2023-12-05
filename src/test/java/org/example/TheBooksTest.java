package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки функциональности класса {@link TheBooks}.
 */
class TheBooksTest implements TheBooksTestInterface{

    /**
     * Экземпляр класса TheBooks для проведения тестов.
     */
    private TheBooks theBooks;

    /**
     * Подготовка данных перед каждым тестом.
     */
    @BeforeEach
    public void setUp() {
        theBooks = new TheBooks();
    }

    /**
     * Тестирование обновления текущей десятки при смене месяца.
     */
    @Test
    public void testUpdateCurrentSetOnMonthChange() {
        // Исходно текущая десятка равна 0
        assertEquals(0, theBooks.getCurrentSet());

        // Сменяем месяц
        LocalDateTime newDate = LocalDateTime.now().plusMonths(1);
        System.out.println(newDate);
        theBooks.setCurrentDate(newDate);

        // После смены месяца текущая десятка должна увеличиться на 1
        assertEquals(1, theBooks.getCurrentSet());
    }
}
