package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TheBooksTest  {

    private long ChatId;
    private MessageHandling bot;

    @Before
    public void setUp() {
        ChatId = 12345L;
        bot = new MessageHandling();
    }


    /**
     * Проверка корректного ответа от бота при использовании команды /vote.
     */
    @Test
    public void voteCommandTest() {
        //проверка корректной работы на 3 этапах - выбора десятки,
        // передачи её в класс BookVoting и работы команды,котору. отправояет пользователь в чат боту


        // Создаем фиксированный DateTimeProvider для теста
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 12, 4, 0, 0);
        DateTimeProvider fixedDateTimeProvider = new FixedDateTimeProvider(fixedDateTime);
        MessageHandling messageHandling = new MessageHandling(fixedDateTimeProvider);
        BookVoting bookVoting = new BookVoting(fixedDateTimeProvider);
        messageHandling.setVotingEndDay(32);


        // Создаем экземпляр TheBooks с использованием фиксированного DateTimeProvider
        TheBooks theBooks = new TheBooks(fixedDateTimeProvider);
        Assert.assertEquals("Количество книг должно быть равно 10", 10, theBooks.getSize());
        Assert.assertEquals("Название первой книги", "\"KGBT+\" - Александр Пелевин", theBooks.getBookByNumber(1).toString());
        Assert.assertEquals("Название последней книги", "\"Клуб убийств по четвергам\" - Ричард Осман", theBooks.getBookByNumber(10).toString());



        // Вызываем метод showBookList и проверяем, что возвращаемая строка содержит правильные книги
        String expectedBookList = " Пожалуйста, выберите 3 книги из списка популярных книг этого месяца ниже, которые вам нравятся больше всего, это поможет нам определить победителя. После этого сообщения отправьте номер первой наиболее понравившейся книги.\n" +
                "1. \"KGBT+\" - Александр Пелевин\n" +
                "2. \"TRANSHUMANISM INC.\" - Виктор Пелевин\n" +
                "3. \"Двадцать тысяч лье под водой\" - Жюль Верн\n" +
                "4. \"Вино из одуванчиков\" - Рэй Брэдбери\n" +
                "5. \"Мастер и Маргарита\" - Михаил Булгаков\n" +
                "6. \"The One. Единственный\" - Джон Маррс\n" +
                "7. \"Атлант расправил плечи: Часть 3\" - Айн Рэнд\n" +
                "8. \"Невидимый гость\" - Эльдар Сафин\n" +
                "9. \"Тайная жизнь домашних животных 2\" - Стив С. Миллер\n" +
                "10. \"Клуб убийств по четвергам\" - Ричард Осман\n";
        String actualBookList = bookVoting.showBookList(ChatId);
        Assert.assertEquals(expectedBookList, actualBookList);



        String response = messageHandling.parseMessage("/vote", ChatId);
        // Ваша проверка
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
        bot.setVotingEndDay(32);
        //здесь нужен новый список из 10 книг
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
        bot.setVotingEndDay(32);
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
     * Проверка отображения результатов голосования командой /voteresults до голосования пользователя.
     */
    @Test
    public void voteResultsCommandTest() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 12, 4, 0, 0);
        DateTimeProvider fixedDateTimeProvider = new FixedDateTimeProvider(fixedDateTime);
        MessageHandling messageHandling = new MessageHandling(fixedDateTimeProvider);
        messageHandling.setVotingEndDay(32);
        String response = messageHandling.parseMessage("/voteresults", ChatId);
        Assert.assertEquals("Статистика голосования:\n" +
                "\"KGBT+\" - Александр Пелевин - 0 голос(ов)\n" +
                "\"TRANSHUMANISM INC.\" - Виктор Пелевин - 0 голос(ов)\n" +
                "\"Двадцать тысяч лье под водой\" - Жюль Верн - 0 голос(ов)\n" +
                "\"Вино из одуванчиков\" - Рэй Брэдбери - 0 голос(ов)\n" +
                "\"Мастер и Маргарита\" - Михаил Булгаков - 0 голос(ов)\n" +
                "\"The One. Единственный\" - Джон Маррс - 0 голос(ов)\n" +
                "\"Атлант расправил плечи: Часть 3\" - Айн Рэнд - 0 голос(ов)\n" +
                "\"Невидимый гость\" - Эльдар Сафин - 0 голос(ов)\n" +
                "\"Тайная жизнь домашних животных 2\" - Стив С. Миллер - 0 голос(ов)\n" +
                "\"Клуб убийств по четвергам\" - Ричард Осман - 0 голос(ов)\n", response);
    }

    /**
     * Проверка отображения результатов голосования командой /voteresults после голосования пользователя.
     */
    @Test
    public void correctVoteResultsCommandTest() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 12, 4, 0, 0);
        DateTimeProvider fixedDateTimeProvider = new FixedDateTimeProvider(fixedDateTime);
        MessageHandling messageHandling = new MessageHandling(fixedDateTimeProvider);
        messageHandling.setVotingEndDay(32);
        messageHandling.parseMessage("/vote", ChatId);
        messageHandling.parseMessage("3", ChatId);
        messageHandling.parseMessage("2", ChatId);
        messageHandling.parseMessage("1", ChatId);
        String response = messageHandling.parseMessage("/voteresults", ChatId);
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
    }

    /**
     * Проверка невозможности использования команды /revote до команды /vote.
     */
    @Test
    public void revoteWrongCommandTest() {
        bot.setVotingEndDay(32);
        String response = bot.parseMessage("/revote", ChatId);
        Assert.assertEquals("Вы не можете использовать эту команду до использования /vote", response);
    }

    /**
     * Проверка корректной работы команды /revote после завершения голосования.
     */
    @Test
    public void revoteCompleteCommandTest() {
        bot.setVotingEndDay(32);
        bot.parseMessage("/vote", ChatId);
        bot.parseMessage("3", ChatId);
        bot.parseMessage("2", ChatId);
        bot.parseMessage("1", ChatId);
        String response = bot.parseMessage("/voteresults", ChatId);
        Assert.assertTrue(response.startsWith("Статистика голосования:"));
        response = bot.parseMessage("/revote", ChatId);
        Assert.assertTrue(response.startsWith(" Пожалуйста, выберите 3 книги из списка популярных книг этого месяца ниже, которые вам нравятся больше всего, это поможет нам определить победителя. После этого сообщения отправьте номер первой наиболее понравившейся книги.\n"));
        bot.parseMessage("6", ChatId);
        bot.parseMessage("7", ChatId);
        bot.parseMessage("8", ChatId);
        response = bot.parseMessage("/voteresults", ChatId);
        Assert.assertTrue(response.startsWith("Статистика голосования:"));
        //тест проверяет верную последовательную обработку команд
    }

    /**
     * Проверка сценария завершения голосования без явного лидера.
     */
    @Test
    public void testEndOfVotingWithNoLeader() {
        bot.setVotingEndDay(LocalDate.now().getDayOfMonth()-1);
        String response = bot.parseMessage("/vote", ChatId);
        Assert.assertEquals("Лидера голосования нет", response);
    }

    /**
     * Проверка сценария завершения голосования с явным лидером.
     */
    @Test
    public void testEndOfVoting() {
        bot.setVotingEndDay(32);
        bot.parseMessage("/vote", ChatId);
        bot.parseMessage("3", ChatId);
        bot.parseMessage("2", ChatId);
        bot.parseMessage("1", ChatId);
        // Устанавливаем значение VOTING_END_DAY
        bot.setVotingEndDay(LocalDate.now().getDayOfMonth()-1);
        String response = bot.parseMessage("/vote", ChatId);
        //тест проверяет, что если существуют какие-то итоги голосования,
        // то есть кто-то из пользователей голосовал, то есть и лидер голосования
        Assert.assertTrue(response.startsWith("Голосование за книгу месяца уже окончено. В этом месяце по итогам голосования читаем:\n"));
    }
}

