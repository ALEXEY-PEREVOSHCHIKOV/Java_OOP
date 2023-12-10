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
     * Поставщик даты и времени для использования в классе.
     */
    private DateTimeProvider dateTimeProvider;

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
     * Конструктор класса с параметрами, принимает поставщика даты и времени.
     *
     * @param dateTimeProvider Поставщик даты и времени
     */
    public BookVoting(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
        theBooks = new TheBooks(dateTimeProvider);
        userVotes = new HashMap<>();
        votingModes = new HashMap<>();
        bookVotesCount = new HashMap<>();
    }


    /**
     * Показывает список книг для голосования.
     *
     * @param chatId идентификатор чата.
     * @return строка с перечислением книг для голосования.
     */
    public String showBookList(long chatId) {
        StringBuilder response = new StringBuilder();
        response.append(" Пожалуйста, выберите 3 книги из списка популярных книг этого месяца ниже, которые вам нравятся больше всего, это поможет нам определить победителя. После этого сообщения отправьте номер первой наиболее понравившейся книги.\n");
        int bookNumber = 1;
        for (int i = 0; i < theBooks.getSize(); i++) {
            response.append(bookNumber).append(". ").append(theBooks.getBookByNumber(bookNumber).toString());
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
        StringBuilder result = new StringBuilder("Голосование за книгу месяца уже окончено. В этом месяце по итогам голосования читаем:\n");
        // Получаем топ-1 книгу с наибольшим числом голосов
        List<TheBooks.Book> topBook = getTopBook();
        for (TheBooks.Book book : topBook) {
            result.append(book).append("\n");
        }
        result.append("Вы можете присоединиться к нам и начать читать вместе! Новое голосование начнётся 1 числа следующего месяца.");
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
     * Возвращает текущий список книг в порядке убывания количества голосов.
     *
     * @return строка с перечислением книг и количеством голосов в порядке убывания.
     */
    public String getVotingStatistics() {
        // Создаем список строк для представления каждой книги с количеством голосов
        List<String> booksWithVotes = new ArrayList<>();
        // Для каждой книги в TheBooks добавляем строку с информацией о книге и количестве голосов
        for (int bookNumber = 1; bookNumber <= theBooks.getSize(); bookNumber++) {
            String bookInfo = theBooks.getBookByNumber(bookNumber).toString();
            int votesCount = bookVotesCount.getOrDefault(bookNumber, 0);
            String bookWithVotes = bookInfo + " - " + votesCount + " голос(ов)";
            booksWithVotes.add(bookWithVotes);
        }
        // Сортируем список по убыванию количества голосов
        booksWithVotes.sort((book1, book2) -> {
            int votesCount1 = Integer.parseInt(book1.substring(book1.lastIndexOf("-") + 1, book1.lastIndexOf(" голос(ов)")).trim());
            int votesCount2 = Integer.parseInt(book2.substring(book2.lastIndexOf("-") + 1, book2.lastIndexOf(" голос(ов)")).trim());
            return Integer.compare(votesCount2, votesCount1);
        });
        // Формируем итоговую строку
        StringBuilder statistics = new StringBuilder("Статистика голосования:\n");
        for (String bookWithVotes : booksWithVotes) {
            statistics.append(bookWithVotes).append("\n");
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

