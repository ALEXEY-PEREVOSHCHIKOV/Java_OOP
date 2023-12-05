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
     * Тестирование не обновления текущей десятки при системной смене времени.
     */
    @Test
    public void testSystemTimeNotAffectToCurrentTenWhenSettingFixedTime() {
        LocalDateTime Date = LocalDateTime.of(2023, 12, 5, 0, 0);
        theBooks.setCurrentDate(Date);
        // Исходно текущая десятка равна 0, текущий месяц - 12;
        // при любом системном времени здесь останется фиксированное значение и месяца и текущей десятки для него
        Assert.assertEquals(0, theBooks.getCurrentSet());
    }


    /**
     * Тестирование обновления текущей десятки при смене месяца в тестах.
     */
    @Test
    public void testUpdateCurrentSetOnMonthChange() {
        LocalDateTime Date = LocalDateTime.of(2023, 12, 5, 0, 0);
        theBooks.setCurrentDate(Date);
        // Исходно текущая десятка равна 0, текущий месяц - 12;
        Assert.assertEquals(0, theBooks.getCurrentSet());
        // Сменяем месяц
        LocalDateTime  newDate = LocalDateTime.of(2024, 1, 5, 0, 0);
        System.out.println(newDate);
        theBooks.setCurrentDate(newDate);
        // После смены месяца текущая десятка должна увеличиться на 1
        Assert.assertEquals(1, theBooks.getCurrentSet());
    }
}


