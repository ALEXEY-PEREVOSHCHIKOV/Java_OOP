package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;


/**
 * Тестовый класс для проверки функциональности класса {@link TheBooks}.
 */
public class TheBooksTest implements TheBooksTestInterface{

    /**
     * Экземпляр класса TheBooks для проведения тестов.
     */
    private TheBooks theBooks;

    /**
     * Подготовка данных перед каждым тестом.
     */
    @Before
    public void setUp() {
        theBooks = new TheBooks();
    }

    /**
     * Тестирование обновления текущей десятки при смене месяца.
     */
    @Test
    public void testUpdateCurrentSetOnMonthChange() {
        // Исходно текущая десятка равна 0, текущий месяц - 12;
        Assert.assertEquals(0, theBooks.getCurrentSet());
        // Сменяем месяц
        LocalDateTime newDate = LocalDateTime.now().plusMonths(1);
        System.out.println(newDate);
        theBooks.setCurrentDate(newDate);
        // После смены месяца текущая десятка должна увеличиться на 1
        Assert.assertEquals(1, theBooks.getCurrentSet());
    }
}

