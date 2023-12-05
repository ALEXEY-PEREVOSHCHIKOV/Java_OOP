package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.*;
import static org.mockito.Mockito.*;

public class BotTest implements BotTestInterface {

    private long ChatId;

    private MessageHandling bot;


    private TheBooks theBooks;

    private PuzzleGame puzzleGame;

    private Map<String, Puzzle> puzzles;

    @Mock
    private Storage storage;

    @InjectMocks
    private MessageHandling messageHandling;

    @Before
    public void setUp() {
        ChatId = 12345L;
        bot = new MessageHandling();
        theBooks = new TheBooks();
        MockitoAnnotations.initMocks(this);
        puzzles = new HashMap<>();
        puzzleGame = new PuzzleGame();
    }

    /**
     * Проверка для команды /genre
     */
    @Test
    public void GenreCommandTest() {
        String response = bot.parseMessage("/genre", ChatId);
        Assert.assertEquals("Здравствуйте, добро пожаловать в бот рекомендации книг! Нажмите /chat и выберите жанр", response);

        /*
         * Проверка для жанра "Научная фантастика"
         */
        response = bot.parseMessage("Научная фантастика", ChatId);
        Assert.assertEquals("Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'", response);

        /*
         * Проверка для жанра "Фэнтези"
         */
        response = bot.parseMessage("Фэнтези", ChatId);
        Assert.assertEquals("Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'", response);

        /*
         * Проверка для жанра "Романтика"
         */
        response = bot.parseMessage("Романтика", ChatId);
        Assert.assertEquals("Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'", response);

        /*
         * Проверка для жанра "Детектив"
         */
        response = bot.parseMessage("Детектив", ChatId);
        Assert.assertEquals("Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'", response);
    }

    /**
     * Проверка ответа для произвольного сообщения
     */
    @Test
    public void AnyMessageTest() {
        String response = bot.parseMessage("Привет", ChatId);
        Assert.assertEquals("Привет", response);
    }


    /**
     * Проверка команды /clearread для полной очистки списка прочитанных книг
     */
    @Test
    public void testClearReadBooksCommand() {
        String response = messageHandling.parseMessage("/clearread", ChatId);
        verify(storage, times(1)).clearReadBooks(ChatId);
        Assert.assertEquals("Список прочитанных книг очищен!", response);
    }


    /**
     * Проверка добавления книги в базу данных при корректном вводе
     */
    @Test
    public void testAddBookCommandWithValidInput() {
        String textMsg = "/addbook";
        when(storage.bookExists(anyString(), anyString(), anyInt(), anyLong())).thenReturn(false);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Введите название книги:", response);
        textMsg = "Sample Book";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Теперь введите автора книги:", response);
        textMsg = "John Doe";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Теперь введите год прочтения книги:", response);
        textMsg = "2023";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Книга 'Sample Book' от автора John Doe (год: 2023) успешно добавлена в список прочитанных!", response);
    }

    /**
     * Проверка, что книга не добавляется, если она уже существует в базе данных
     */
    @Test
    public void testAddBookCommandWithExistingBook() {
        String textMsg = "/addbook";
        when(storage.bookExists(anyString(), anyString(), anyInt(), anyLong())).thenReturn(true);
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "Sample Book";
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "John Doe";
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "2023";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addReadBook(anyString(), anyString(), anyInt(), anyLong());
        Assert.assertEquals("Книга с указанным названием, автором и годом прочтения уже существует в базе данных.", response);
    }


    /**
     * Проверка случая, когда год вводится в неверном формате
     */
    @Test
    public void testAddBookCommandWithInvalidYear() {
        String textMsg = "/addbook";
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "Sample Book";
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "John Doe";
        messageHandling.parseMessage(textMsg, ChatId);
        textMsg = "НЕ ГОД";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addReadBook(anyString(), anyString(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат года прочтения. Пожалуйста, введите год цифрами.", response);
    }


    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при пустом списке
     */
    @Test
    public void testGetReadBooksCommandWithEmptyList() {
        ArrayList<String> emptyList = new ArrayList<>();
        when(storage.getReadBooks(ChatId)).thenReturn(emptyList);
        String response = messageHandling.parseMessage("/getread", ChatId);
        verify(storage, times(1)).getReadBooks(ChatId);
        Assert.assertEquals("Список прочитанных книг пуст.", response);
    }


    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при заполненном списке
     */
    @Test
    public void testGetReadBooksCommandWithNonEmptyList() {
        ArrayList<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("Book 1");
        nonEmptyList.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(nonEmptyList);
        String response = messageHandling.parseMessage("/getread", ChatId);
        verify(storage, times(1)).getReadBooks(ChatId);
        Assert.assertEquals("Прочитанные книги:\n1. Book 1\n2. Book 2\n", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда автор указан верно
     */
    @Test
    public void testGetBooksByAuthorCommandWithExistingBooks() {
        String author = "John Doe";
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.getBooksByAuthor(author, ChatId)).thenReturn(books);
        messageHandling.parseMessage("/getbyauthor", ChatId);
        String response = messageHandling.parseMessage(author, ChatId);
        verify(storage, times(1)).getBooksByAuthor(author, ChatId);
        Assert.assertEquals("Книги автора John Doe:\n" + "\"Book 1\";\n" + "\"Book 2\";\n", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда автор указан неверно
     */
    @Test
    public void testGetBooksByAuthorCommandWithNoBooks() {
        String author = "Nonexistent Author";
        when(storage.getBooksByAuthor(author, ChatId)).thenReturn(new ArrayList<>());
        messageHandling.parseMessage("/getbyauthor", ChatId);
        String response = messageHandling.parseMessage(author, ChatId);
        verify(storage, times(1)).getBooksByAuthor(author, ChatId);
        Assert.assertEquals("Нет прочитанных книг этого автора.", response);
    }


    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан неверно
     */
    @Test
    public void testGetBooksByYearCommandWithNoBooks() {
        int year = 1112;
        when(storage.getBooksByYear(year, ChatId)).thenReturn(new ArrayList<>());
        messageHandling.parseMessage("/getbyyear", ChatId);
        String response = messageHandling.parseMessage(String.valueOf(year), ChatId);
        verify(storage, times(1)).getBooksByYear(year, ChatId);
        Assert.assertEquals("Нет прочитанных книг в этом году.", response);
    }

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан верно
     */
    @Test
    public void testGetBooksByYearCommandWithExistingBooks() {
        int year = 2020;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.getBooksByYear(year, ChatId)).thenReturn(books);
        messageHandling.parseMessage("/getbyyear", ChatId);
        String response = messageHandling.parseMessage(String.valueOf(year), ChatId);
        verify(storage, times(1)).getBooksByYear(year, ChatId);
        Assert.assertEquals("Книги 2020 года:\n" + "\"Book 1\";\n" + "\"Book 2\";\n", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан верно
     */
    @Test
    public void testRemoveBookCommandWithValidBookNumber() {
        String message = "1";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1");
        readBooks.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(readBooks);
        messageHandling.parseMessage("/removebook", ChatId);
        String response = messageHandling.parseMessage(message, ChatId);
        verify(storage, times(1)).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Книга Book 1 успешно удалена из списка прочитанных!", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан неверно
     */
    @Test
    public void testRemoveBookCommandWithInvalidBookNumber() {
        String message = "3";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1");
        readBooks.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(readBooks);
        messageHandling.parseMessage("/removebook", ChatId);
        String response = messageHandling.parseMessage(message, ChatId);
        verify(storage, never()).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Указанный уникальный номер книги не существует в списке прочитанных книг.", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда указано не число
     */
    @Test
    public void testRemoveBookCommandWithInvalidFormat() {
        String message = "InvalidNumber";
        messageHandling.parseMessage("/removebook", ChatId);
        String response = messageHandling.parseMessage(message, ChatId);
        verify(storage, never()).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Некорректный формат номера книги.", response);
    }


    /**
     * Проверка команды /editbook для случая, когда выполняется успешное редактирование книги с правильными данными
     */
    @Test
    public void testEditBookCommandWithValidData() {
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Old Book\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(readBooks);
        messageHandling.parseMessage("/editbook", ChatId);
        messageHandling.parseMessage("1", ChatId);
        messageHandling.parseMessage("New Book", ChatId);
        messageHandling.parseMessage("New Author", ChatId);
        String response = messageHandling.parseMessage("2023", ChatId);
        verify(storage, times(1)).editReadBook(eq("Old Book"), eq("Old Author"), eq(2022), eq("New Book"), eq("New Author"), eq(2023), eq(ChatId));
        Assert.assertEquals("Книга 'Old Book' успешно отредактирована в списке прочитанных!", response);
    }


    /**
     * Проверка команды /editbook для случая, когда указанный номер книги недопустим (например, больше размера списка)
     */
    @Test
    public void testEditBookCommandWithInvalidBookNumber() {
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Old Book\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(readBooks);
        messageHandling.parseMessage("/editbook", ChatId);
        messageHandling.parseMessage("3", ChatId);
        messageHandling.parseMessage("New Book", ChatId);
        messageHandling.parseMessage("New Author", ChatId);
        String response = messageHandling.parseMessage("2023", ChatId);
        verify(storage, never()).editReadBook(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Указанный уникальный номер книги не существует в списке прочитанных книг.", response);
    }


    /**
     * Проверка команды /editbook для случая, когда данные книги введены в неверном формате.
     */
    @Test
    public void testEditBookCommandWithInvalidDataFormat() {
        String message = "InvalidData";
        messageHandling.parseMessage("/editbook", ChatId);
        String response = messageHandling.parseMessage(message, ChatId);
        verify(storage, never()).editReadBook(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Некорректный формат номера книги.", response);
    }


    /**
     * Проверяет команду получения цитаты.
     */
    @Test
    public void CitationCommandTest() {
        String response = bot.parseMessage("/get", ChatId);
        Assert.assertTrue(response.startsWith("Цитата:"));
    }


    /**
     * Проверяет команду /playpuzzle - начала игры в загадки.
     */
    @Test
    public void playPuzzleCommandTest() {
        String response = bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        Assert.assertTrue(response.startsWith("Добро пожаловать в игру в загадки! Начнем."));
    }


    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой начала игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    public void testPlayPuzzleContainsRandomPuzzle() {
        puzzles = puzzleGame.getPuzzles();
        String response = bot.parseMessage("/playpuzzle", ChatId);
        String puzzleQuestion = response.replace("Добро пожаловать в игру в загадки! Начнем.\nЗагадка: ", "");
        Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
    }

    /**
     * Проверяет команду /gethint - получения подсказки в игре в загадки.
     */
    @Test
    public void getHintCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        String response = bot.parseMessage("/gethint", ChatId); //получение подсказки к текущей загадке
        Assert.assertTrue(response.startsWith("Подсказка:"));
    }

    /**
     * Проверяет команду получения следующей загадки в игре.
     */
    @Test
    public void anotherRiddleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/anotheriddle", ChatId);
        Assert.assertTrue(response.startsWith("Следующая загадка:"));
    }


    /**
     * Тестирует команду /anotheriddle для проверки, что произвольная загадка, вызываемая в сообщении командой следующая загадка в игре в загадки
     * содержится в списке всех загадок
     */
    @Test
    public void testAnotheRiddleContainsRandomPuzzle() {
        puzzles = puzzleGame.getPuzzles();
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/anotheriddle", ChatId);
        String puzzleQuestion = response.replace("Следующая загадка: ", "");
        Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
    }

    /**
     * Проверяет команду /restart - перезапуска игры в загадки.
     */
    @Test
    public void restartCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/restart", ChatId);
        Assert.assertTrue(response.startsWith("Игра в загадки начата заново."));
    }


    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой перезапуска игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    public void testRestartContainsRandomPuzzle() {
        puzzles = puzzleGame.getPuzzles();
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/restart", ChatId);
        String puzzleQuestion = response.replace("Игра в загадки начата заново.\n" + "Добро пожаловать в игру в загадки! Начнем.\nЗагадка: ", "");
        Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
    }


    /**
     * Проверяет команду /getanswer получения ответа на текущую загадку в игре.
     */
    @Test
    public void getAnswerCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/getanswer", ChatId);
        Assert.assertTrue(response.contains("Ответ на загадку"));
    }

    /**
     * Проверяет команду завершения режима головоломки.
     */
    @Test
    public void stopPuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/stoppuzzle", ChatId);
        Assert.assertEquals("Режим головоломки завершен.\nПравильных ответов: 0\n" + "Неправильных ответов: 20\n" + "Процент правильных ответов: 0.0%", response);
    }

    /**
     * Проверка корректного ответа от бота при использовании команды /vote.
     */
    @Test
    public void voteCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        String response = bot.parseMessage("/vote", ChatId);
        Assert.assertEquals(" Здравствуйте, добро пожаловать на ежемесячное голосование за “книгу месяца”, которое проводится с 1 по 5 число. Вам предлагается на выбор 10 книг.\n" +
                " Пожалуйста, выберите 3 книги из списка популярных книг этого месяца ниже, которые вам нравятся больше всего, это поможет нам определить победителя. После этого сообщения отправьте номер первой наиболее понравившейся книги.\n" +
                "1. \"KGBT+\" - Александр Пелевин\n" +
                "2. \"TRANSHUMANISM INC.\" - Виктор Пелевин\n" +
                "3. \"Двадцать тысяч лье под водой\" - Жюль Верн\n" +
                "4. \"Вино из одуванчиков\" - Рэй Брэдбери\n" +
                "5. \"Мастер и Маргарита\" - Михаил Булгаков\n" +
                "6. \"The One. Единственный\" - Джон Маррс\n" +
                "7. \"Атлант расправил плечи: Часть 3\" - Айн Рэнд\n" +
                "8. \"Невидимый гость\" - Эльдар Сафин\n" +
                "9. \"Тайная жизнь домашних животных 2\" - Стив С. Миллер\n" +
                "10. \"Клуб убийств по четвергам\" - Ричард Осман\n", response);
    }

    /**
     * Проверка последовательного голосования пользователя и корректных ответов бота.
     */
    @Test
    public void voteAfterCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        bot.parseMessage("/vote", ChatId);
        String response = bot.parseMessage("3", ChatId);
        Assert.assertEquals("Вы выбрали книгу номер 3. Выберите еще 2 книг(и).", response);
        response = bot.parseMessage("2", ChatId);
        Assert.assertEquals("Вы выбрали книгу номер 2. Выберите еще 1 книг(и).", response);
        response = bot.parseMessage("1", ChatId);
        Assert.assertEquals("Спасибо за ваш голос!", response);
    }

    /**
     * Проверка обработки некорректных вводов пользователя в процессе голосования.
     */
    @Test
    public void voteAfterWrongEnterCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        bot.parseMessage("/vote", ChatId);
        String response = bot.parseMessage("3", ChatId);
        Assert.assertEquals("Вы выбрали книгу номер 3. Выберите еще 2 книг(и).", response);
        response = bot.parseMessage("3", ChatId);
        Assert.assertEquals("Вы уже выбрали эту книгу. Выберите другую.", response);
        response = bot.parseMessage("re", ChatId);
        Assert.assertEquals("Введите число от 1 до 10.", response);
        response = bot.parseMessage("19", ChatId);
        Assert.assertEquals("Неверный номер книги. Выберите номер от 1 до 10.", response);
        response = bot.parseMessage("2", ChatId);
        Assert.assertEquals("Вы выбрали книгу номер 2. Выберите еще 1 книг(и).", response);
        response = bot.parseMessage("1", ChatId);
        Assert.assertEquals("Спасибо за ваш голос!", response);
        response = bot.parseMessage("/vote", ChatId);
        Assert.assertEquals("Если вы пытаетесь проголосовать повторно, то этого сделать нельзя. Если вы хотите переголосовать, нажмите /revote", response);
    }

    /**
     * Проверка корректного отображения результатов голосования командой /voteresults.
     */
    @Test
    public void voteResultsCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        String response = bot.parseMessage("/voteresults", ChatId);
        Assert.assertTrue(response.startsWith("Статистика голосования:\n" + "\"KGBT+\" - Александр Пелевин - "));
    }

    /**
     * Проверка невозможности использования команды /revote до команды /vote.
     */
    @Test
    public void revoteWrongCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        String response = bot.parseMessage("/revote", ChatId);
        Assert.assertEquals("Вы не можете использовать эту команду до использования /vote", response);
    }

    /**
     * Проверка корректной работы команды /revote после завершения голосования.
     */
    @Test
    public void revoteCompleteCommandTest() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        bot.parseMessage("/vote", ChatId);
        bot.parseMessage("3", ChatId);
        bot.parseMessage("2", ChatId);
        bot.parseMessage("1", ChatId);
        String response = bot.parseMessage("/voteresults", ChatId);
        Assert.assertEquals("Статистика голосования:\n" +
                "\"KGBT+\" - Александр Пелевин - 1 голос(ов)\n" +
                "\"TRANSHUMANISM INC.\" - Виктор Пелевин - 1 голос(ов)\n" +
                "\"Двадцать тысяч лье под водой\" - Жюль Верн - 1 голос(ов)\n" +
                "\"Вино из одуванчиков\" - Рэй Брэдбери - 0 голос(ов)\n" +
                "\"Мастер и Маргарита\" - Михаил Булгаков - 0 голос(ов)\n" +
                "\"The One. Единственный\" - Джон Маррс - 0 голос(ов)\n" +
                "\"Атлант расправил плечи: Часть 3\" - Айн Рэнд - 0 голос(ов)\n" +
                "\"Невидимый гость\" - Эльдар Сафин - 0 голос(ов)\n" +
                "\"Тайная жизнь домашних животных 2\" - Стив С. Миллер - 0 голос(ов)\n" +
                "\"Клуб убийств по четвергам\" - Ричард Осман - 0 голос(ов)\n", response);
        response = bot.parseMessage("/revote", ChatId);
        Assert.assertEquals(" Пожалуйста, выберите 3 книги из списка популярных книг этого месяца ниже, которые вам нравятся больше всего, это поможет нам определить победителя. После этого сообщения отправьте номер первой наиболее понравившейся книги.\n" +
                "1. \"KGBT+\" - Александр Пелевин\n" +
                "2. \"TRANSHUMANISM INC.\" - Виктор Пелевин\n" +
                "3. \"Двадцать тысяч лье под водой\" - Жюль Верн\n" +
                "4. \"Вино из одуванчиков\" - Рэй Брэдбери\n" +
                "5. \"Мастер и Маргарита\" - Михаил Булгаков\n" +
                "6. \"The One. Единственный\" - Джон Маррс\n" +
                "7. \"Атлант расправил плечи: Часть 3\" - Айн Рэнд\n" +
                "8. \"Невидимый гость\" - Эльдар Сафин\n" +
                "9. \"Тайная жизнь домашних животных 2\" - Стив С. Миллер\n" +
                "10. \"Клуб убийств по четвергам\" - Ричард Осман\n", response);
        bot.parseMessage("6", ChatId);
        bot.parseMessage("7", ChatId);
        bot.parseMessage("8", ChatId);
        response = bot.parseMessage("/voteresults", ChatId);
        Assert.assertEquals("Статистика голосования:\n" +
                "\"The One. Единственный\" - Джон Маррс - 1 голос(ов)\n" +
                "\"Атлант расправил плечи: Часть 3\" - Айн Рэнд - 1 голос(ов)\n" +
                "\"Невидимый гость\" - Эльдар Сафин - 1 голос(ов)\n" +
                "\"KGBT+\" - Александр Пелевин - 0 голос(ов)\n" +
                "\"TRANSHUMANISM INC.\" - Виктор Пелевин - 0 голос(ов)\n" +
                "\"Двадцать тысяч лье под водой\" - Жюль Верн - 0 голос(ов)\n" +
                "\"Вино из одуванчиков\" - Рэй Брэдбери - 0 голос(ов)\n" +
                "\"Мастер и Маргарита\" - Михаил Булгаков - 0 голос(ов)\n" +
                "\"Тайная жизнь домашних животных 2\" - Стив С. Миллер - 0 голос(ов)\n" +
                "\"Клуб убийств по четвергам\" - Ричард Осман - 0 голос(ов)\n", response);
    }

    /**
     * Проверка сценария завершения голосования без явного лидера.
     */
    @Test
    public void testEndOfVotingWithNoLeader() {
        // Устанавливаем значение VOTING_END_DAY
        int votingEndDay = 3;
        bot.setVotingEndDay(votingEndDay);
        // Вызываем ваш метод, который зависит от текущей даты
        String response = bot.parseMessage("/vote", ChatId);
        // Проверяем ожидаемый результат (ваша логика зависит от текущей даты)
        Assert.assertEquals("Лидера голосования нет", response);
    }

    /**
     * Проверка сценария завершения голосования с явным лидером.
     */
    @Test
    public void testEndOfVoting() {
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 4, 0, 0);
        bot.setCurrentDate(newDate);
        bot.parseMessage("/vote", ChatId);
        bot.parseMessage("3", ChatId);
        bot.parseMessage("2", ChatId);
        bot.parseMessage("1", ChatId);
        // Устанавливаем значение VOTING_END_DAY
        int votingEndDay = 3;
        bot.setVotingEndDay(votingEndDay);
        // Вызываем ваш метод, который зависит от текущей даты
        String response = bot.parseMessage("/vote", ChatId);
        // Проверяем ожидаемый результат (ваша логика зависит от текущей даты)
        Assert.assertEquals("Голосование за книгу месяца уже окончено. В этом месяце по итогам голосования читаем:\n" +
                "\"Двадцать тысяч лье под водой\" - Жюль Верн\n" +
                "Вы можете присоединиться к нам и начать читать вместе! Новое голосование начнётся 1 числа следующего месяца."
                , response);
    }
}
