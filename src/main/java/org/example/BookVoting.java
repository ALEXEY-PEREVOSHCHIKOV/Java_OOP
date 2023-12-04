package org.example;
import java.util.*;

/**
 * Класс, представляющий систему голосования за книги.
 */
public class BookVoting implements BookVotingInterface{

    /**
     * Экземпляр класса, управляющего списком книг.
     */
    private TheBooks theBooks;

    /**
     * Map для хранения голосов пользователей по каждой книге.
     */
    private Map<Long, List<Integer>> userVotes;

    /**
     * Map, определяющая режим голосования для каждого чата.
     */
    private Map<Long, Boolean> votingModes;

    /**
     * Map для хранения количества голосов для каждой книги.
     */
    private Map<Integer, Integer> bookVotesCount; // Добавили для хранения количества голосов для каждой книги

    /**
     * Конструктор класса BookVoting. Инициализирует все необходимые структуры данных.
     */
    public BookVoting() {
        theBooks = new TheBooks();
        userVotes = new HashMap<>();
        votingModes = new HashMap<>();
        bookVotesCount = new HashMap<>(); // Инициализируем мапу для хранения количества голосов
    }


    /**
     * Показывает список книг для голосования.
     *
     * @param chatId идентификатор чата.
     * @return строка с перечислением книг для голосования.
     */
    public String showBookList(long chatId) {
        StringBuilder response = new StringBuilder();
        response.append("Выберите 3 понравившиеся книги, отправив их номера последовательно:\n");
        int bookNumber = 1;
        for (int i = 0; i < theBooks.getSize(); i++) {
            response.append(bookNumber).append(". ").append(theBooks.getBookByNumber(bookNumber).toString());
            // Добавим количество голосов для текущей книги только при запросе результатов
            if (votingModes.getOrDefault(chatId, false)) {
                int votesCount = bookVotesCount.getOrDefault(bookNumber, 0);
                response.append(" - ").append(votesCount).append(" голосов");
            }
            response.append("\n");
            bookNumber++;
        }
        return response.toString();
    }


    /**
     * Обрабатывает голоса пользователя.
     *
     * @param textMsg текстовое сообщение пользователя.
     * @param chatId  идентификатор чата.
     * @return ответ на голосование пользователя.
     */
    public String processUserVotes(String textMsg, long chatId) {
        List<Integer> votes = userVotes.computeIfAbsent(chatId, k -> new ArrayList<>());
        try {
            int selectedBookNumber = Integer.parseInt(textMsg);
            if (selectedBookNumber >= 1 && selectedBookNumber <= theBooks.getSize()) {
                if (!votes.contains(selectedBookNumber)) {
                    votes.add(selectedBookNumber);
                    // Увеличиваем количество голосов для выбранной книги
                    bookVotesCount.put(selectedBookNumber, bookVotesCount.getOrDefault(selectedBookNumber, 0) + 1);
                    if (votes.size() == 3) {
                        return "Спасибо за ваш голос!";
                    } else {
                        return "Вы выбрали книгу номер " + selectedBookNumber + ". Выберите еще " + (3 - votes.size()) + " книг(и).";
                    }
                } else {
                    return "Вы уже выбрали эту книгу. Выберите другую.";
                }
            } else {
                return "Неверный номер книги. Выберите номер от 1 до " + theBooks.getSize() + ".";
            }
        } catch (NumberFormatException e) {
            return "Введите число от 1 до " + theBooks.getSize() + ".";
        }
    }


    /**
     * Получает отсортированный список голосов за книги.
     *
     * @return отсортированный список голосов.
     */
    private List<Map.Entry<Integer, Integer>> getSortedVotes() {
        Map<Integer, Integer> bookVotes = new HashMap<>();
        // Подсчитываем голоса для каждой книги
        for (List<Integer> userVotes : userVotes.values()) {
            for (int i = 0; i < userVotes.size(); i++) {
                int bookNumber = userVotes.get(i);
                bookVotes.put(bookNumber, bookVotes.getOrDefault(bookNumber, 0) + (3 - i));
            }
        }
        // Сортируем книги по количеству голосов в убывающем порядке
        List<Map.Entry<Integer, Integer>> sortedVotes = new ArrayList<>(bookVotes.entrySet());
        sortedVotes.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return sortedVotes;
    }


    /**
     * Завершает голосование и возвращает результат.
     *
     * @return результат голосования.
     */
    public String finishVoting() {
        StringBuilder result = new StringBuilder("Голосование окончено.Итоги голосования:\n");
        // Получаем топ-1 книгу с наибольшим числом голосов
        List<TheBooks.Book> topBook = getTopBook();
        result.append("Книга с наибольшим числом голосов:\n");
        for (TheBooks.Book book : topBook) {
            result.append(book).append("\n");
        }
        result.append("Вы можете присоединиться к нам и начать читать вместе!\n");
        return result.toString();
    }


    /**
     * Получает топовую книгу с наибольшим числом голосов.
     *
     * @return список топовых книг.
     */
    private List<TheBooks.Book> getTopBook() {
        List<Map.Entry<Integer, Integer>> sortedVotes = getSortedVotes();
        // Получаем максимальное количество голосов
        int maxVotes = sortedVotes.get(0).getValue();
        // Фильтруем книги с максимальным количеством голосов
        List<Map.Entry<Integer, Integer>> topBooks = sortedVotes.stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .toList();
        // Получаем первую книгу среди книг с одинаковым максимальным количеством голосов
        int topBookNumber = topBooks.get(0).getKey();
        return Collections.singletonList(theBooks.getBookByNumber(topBookNumber));
    }


    /**
     * Получает статистику голосования.
     *
     * @return статистика голосования.
     */
    public String getVotingStatistics() {
        StringBuilder statistics = new StringBuilder();
        int bookNumber = 1;

        for (int i = 0; i < theBooks.getSize(); i++) {
            statistics.append(bookNumber).append(" ").append(theBooks.getBookByNumber(bookNumber).toString());

            // Добавим количество голосов для текущей книги
            int votesCount = bookVotesCount.getOrDefault(bookNumber, 0);
            statistics.append(" - ").append(votesCount).append(" голосов");

            statistics.append("\n");
            bookNumber++;
        }
        return statistics.toString();
    }


    /**
     * Отменяет голоса пользователя.
     *
     * @param chatId идентификатор чата пользователя.
     */
    public void cancelUserVotes(long chatId) {
        if (userVotes.containsKey(chatId)) {
            List<Integer> votes = userVotes.get(chatId);
            for (int selectedBookNumber : votes) {
                // Уменьшаем количество голосов для выбранной книги
                int currentVotesCount = bookVotesCount.getOrDefault(selectedBookNumber, 0);
                if (currentVotesCount > 0) {
                    bookVotesCount.put(selectedBookNumber, currentVotesCount - 1);
                }
            }
            userVotes.remove(chatId);
        }
    }
}


