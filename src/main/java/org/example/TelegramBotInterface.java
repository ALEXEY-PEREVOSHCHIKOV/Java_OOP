package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Интерфейс для Телеграмм-бота.
 */
public interface TelegramBotInterface {

    /**
     * Создание клавиатуры в боте.
     *
     * @return Объект ReplyKeyboardMarkup с настроенной клавиатурой.
     */
    ReplyKeyboardMarkup createKeyboard();
}

