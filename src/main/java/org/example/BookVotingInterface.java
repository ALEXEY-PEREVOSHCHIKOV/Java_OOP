package org.example;

/**
 * Интерфейс для класса управления голосованием по книгам {@link BookVoting}.
 */
public interface BookVotingInterface {

    /**
     * Показывает список книг для голосования.
     *
     * @param chatId идентификатор чата.
     * @return строка с перечислением книг для голосования.
     */
    String showBookList(long chatId);

    /**
     * Обрабатывает голоса пользователя.
     *
     * @param textMsg текстовое сообщение пользователя.
     * @param chatId  идентификатор чата.
     * @return ответ на голосование пользователя.
     */
    String processUserVotes(String textMsg, long chatId);

    /**
     * Завершает голосование и возвращает результат.
     *
     * @return результат голосования.
     */
    String finishVoting();

    /**
     * Получает статистику голосования.
     *
     * @return статистика голосования.
     */
    String getVotingStatistics();

    /**
     * Отменяет голоса пользователя.
     *
     * @param chatId идентификатор чата пользователя.
     */
    void cancelUserVotes(long chatId);
}
