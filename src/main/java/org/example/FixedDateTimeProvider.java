package org.example;

import java.time.LocalDateTime;

public class FixedDateTimeProvider implements DateTimeProvider {

    private final LocalDateTime fixedDateTime;

    public FixedDateTimeProvider(LocalDateTime fixedDateTime) {
        this.fixedDateTime = fixedDateTime;
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        return fixedDateTime;
    }
}