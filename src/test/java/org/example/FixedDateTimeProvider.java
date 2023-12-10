package org.example;

import java.time.LocalDateTime;

/**
 * Класс, предоставляющий фиксированное значение даты и времени в качестве реализации интерфейса DateTimeProvider.
 */
public class FixedDateTimeProvider implements DateTimeProvider {

    /**
     * Фиксированное значение даты и времени.
     */
    private final LocalDateTime fixedDateTime;

    /**
     * Конструктор класса, инициализирующий объект с заданным фиксированным значением даты и времени.
     *
     * @param fixedDateTime Фиксированное значение даты и времени
     */
    public FixedDateTimeProvider(LocalDateTime fixedDateTime) {
        this.fixedDateTime = fixedDateTime;
    }

    /**
     * Возвращает фиксированное значение даты и времени.
     *
     * @return Заданное фиксированное значение даты и времени
     */
    @Override
    public LocalDateTime getCurrentDateTime() {
        return fixedDateTime;
    }
}