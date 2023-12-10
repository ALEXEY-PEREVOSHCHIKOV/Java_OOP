package org.example;

import java.time.LocalDateTime;

/**
 * Класс, предоставляющий текущее системное значение даты и времени в качестве реализации интерфейса DateTimeProvider.
 */
public class DefaultDateTimeProvider implements DateTimeProvider {

    /**
     * Возвращает текущее системное значение даты и времени.
     *
     * @return Текущее системное значение даты и времени
     */
    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
