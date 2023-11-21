package org.example;
import org.junit.Test;


/**
 * Интерфейс для тестирования функционала бота.
 */

public interface BotTestInterface {
    /**
     * Подготавливает необходимые ресурсы перед выполнением тестов.
     */
    void setUp();

    /**
     * Проверка функционала команды жанра.
     */
    @Test
    void GenreCommandTest();

    /**
     * Проверка ответа на произвольное сообщение.
     */
    @Test
    void AnyMessageTest();

    /**
     * Проверка команды для полной очистки списка прочитанных книг.
     */
    @Test
    void testClearReadBooksCommand();

    /**
     * Проверка добавления книги в базу данных при корректном вводе
     */
    @Test
    void testAddBookCommandWithValidInput();

    /**
     * Проверка, что книга не добавляется, если она уже существует в базе данных
     */
    @Test
    void testAddBookCommandWithExistingBook();

    /**
     * Проверка случая, когда год вводится в неверном формате
     */
    @Test
    void testAddBookCommandWithInvalidYear();

    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при пустом списке
     */
    @Test
    void testGetReadBooksCommandWithEmptyList();

    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при заполненном списке
     */
    @Test
    void testGetReadBooksCommandWithNonEmptyList();

    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда автор указан верно
     */
    @Test
    void testGetBooksByAuthorCommandWithExistingBooks();

    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда авор указан неверно
     */
    @Test
    void testGetBooksByAuthorCommandWithNoBooks();

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан неверно
     */
    @Test
    void testGetBooksByYearCommandWithNoBooks();

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан верно
     */
    @Test
    void testGetBooksByYearCommandWithExistingBooks();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан верно
     */
    @Test
    void testRemoveBookCommandWithValidBookNumber();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан неверно
     */
    @Test
    void testRemoveBookCommandWithInvalidBookNumber();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда указано не число
     */
    @Test
    void testRemoveBookCommandWithInvalidFormat();


    /**
     * Проверка команды /editbook для случая, когда выполняется успешное редактирование книги с правильными данными
     */

    @Test
    void testEditBookCommandWithValidData();



    /**
     * Проверка команды /editbook для случая, когда указанный номер книги недопустим (например, больше размера списка)
     */

    @Test
    void testEditBookCommandWithInvalidBookNumber();

    /**
     * Проверка команды /editbook для случая, когда данные книги введены в неверном формате.
     */

    @Test
    void testEditBookCommandWithInvalidDataFormat();

    /**
     * Проверяет команду получения цитаты.
     */
    @Test
    void CitationCommandTest();

    /**
     * Проверяет команду /playpuzzle - начала игры в загадки.
     */
    @Test
    void playPuzzleCommandTest();

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой начала игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testPlayPuzzleContainsRandomPuzzle();


    /**
     * Проверяет команду /gethint - получения подсказки в игре в загадки.
     */
    @Test
    void getHintCommandTest();

    /**
     * Проверяет команду получения следующей загадки в игре.
     */
    @Test
    void anotherRiddleCommandTest();

    /**
     * Тестирует команду /anotheriddle для прокерки, что произвольная загадка, вызываемая в сообщении командой следующая загадка в игре в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testAnotheRiddleContainsRandomPuzzle();


    /**
     * Проверяет команду /restart - перезапуска игры в загадки.
     */
    @Test
    void restartCommandTest();

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой перезапуска игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testRestartContainsRandomPuzzle();

    /**
     * Проверяет команду /getanswer получения ответа на текущую загадку в игре.
     */
    @Test
    void getAnswerCommandTest();


    /**
     * Проверяет команду завершения режима головоломки.
     */
    @Test
    void stopPuzzleCommandTest();
}


