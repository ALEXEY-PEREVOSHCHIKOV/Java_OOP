package org.example;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime getCurrentDateTime();
}