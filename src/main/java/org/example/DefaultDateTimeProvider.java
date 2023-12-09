package org.example;

import java.time.LocalDateTime;

public class DefaultDateTimeProvider implements DateTimeProvider {

    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}