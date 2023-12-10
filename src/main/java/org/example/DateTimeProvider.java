package org.example;

import java.time.LocalDateTime;

/**
 * Интерфейс, предоставляющий метод для получения текущего значения даты и времени.
 */
public interface DateTimeProvider {

    /**
     * Возвращает текущее системное значение даты и времени.
     *
     * @return Текущее системное значение даты и времени
     */
    LocalDateTime getCurrentDateTime();
}
