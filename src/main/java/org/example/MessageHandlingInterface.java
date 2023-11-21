package org.example;

/**
 * Интерфейс для обработки сообщений пользователя
 */
public interface MessageHandlingInterface {
    /**
     * Обрабатывает текстовое сообщение пользователя и возвращает ответ.
     *
     * @param textMsg Текстовое сообщение пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на сообщение пользователя.
     */
    String parseMessage (String textMsg, long chatId);
}
