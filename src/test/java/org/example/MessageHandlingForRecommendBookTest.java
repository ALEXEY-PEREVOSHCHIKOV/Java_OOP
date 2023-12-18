package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import java.util.*;



/**
 * Класс для тестирования обработки сообщений в контексте работы со списком прочитанных книг.
 */
public class MessageHandlingForRecommendBookTest {

    /**
     * Идентификатор чата для тестирования.
     */
    private long ChatId;


    @Mock
    private Storage storage;


    @InjectMocks
    private MessageHandling messageHandling = new MessageHandling();

    /**
     * Метод, выполняемый перед каждым тестом, инициализирует идентификатор чата
     */
    @Before
    public void setUp() {
        ChatId = 12345L;
        MockitoAnnotations.initMocks(this);

    }

    /**
     * Тестирование команды /recommendbook с корректным вводом.
     */
    @Test
    public void testRecommendBookCommandWithValidInput() {
        String textMsg = "/recommendbook";
        when(storage.recBookExists(anyString(), anyString())).thenReturn(false);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Введите название книги:", response);
        textMsg = "Sample Book";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Теперь введите автора книги:", response);
        textMsg = "John Doe";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Выберите жанр, который наиболее близок к вашей книге из списка ниже и напишите мне его следующим сообщением:\n" +
                "Драма\n" +
                "Приключения \n" +
                "Фэнтези \n" +
                "Научная Фантастика \n" +
                "История \n" +
                "Ужасы \n" +
                "Детектив \n" +
                "Сказка \n" +
                "Романтика\n", response);
        textMsg = "Детектив";
        response = messageHandling.parseMessage(textMsg, ChatId);
        Assert.assertEquals("Книга 'Sample Book' от автора John Doe (жанр: Детектив) успешно добавлена в список!", response);
    }


    /**
     * Тестирование команды /recommendbook с некорректным вводом.
     */
    @Test
    public void testRecommendBookCommandWithInvalidInput() {
        String textMsg = "/recommendbook";
        when(storage.recBookExists(anyString(), anyString())).thenReturn(false);
        messageHandling.parseMessage(textMsg, ChatId);
        messageHandling.parseMessage("Sample Book", ChatId);
        String response = messageHandling.parseMessage("John Doe", ChatId);
        Assert.assertEquals("Выберите жанр, который наиболее близок к вашей книге из списка ниже и напишите мне его следующим сообщением:\n" +
                "Драма\n" +
                "Приключения \n" +
                "Фэнтези \n" +
                "Научная Фантастика \n" +
                "История \n" +
                "Ужасы \n" +
                "Детектив \n" +
                "Сказка \n" +
                "Романтика\n", response);
        response = messageHandling.parseMessage("WRONG", ChatId);
        Assert.assertEquals("Неверный жанр, выберите жанр из списка выше.", response);
        response = messageHandling.parseMessage("История", ChatId);
        Assert.assertEquals("Книга 'Sample Book' от автора John Doe (жанр: История) успешно добавлена в список!", response);
    }


    /**
     * Тестирование команды /recommendbook для уже существующей книги.
     */
    @Test
    public void testRecommendBookCommandWithExistingBook() {
        String textMsg = "/recommendbook";
        when(storage.recBookExists(anyString(), anyString())).thenReturn(true);
        messageHandling.parseMessage(textMsg, ChatId);
        messageHandling.parseMessage("Sample Book", ChatId);
        messageHandling.parseMessage("John Doe", ChatId);
        String response = messageHandling.parseMessage("История", ChatId);
        Assert.assertEquals("Книга с указанным названием и автором уже существует в базе данных.", response);
    }


    /**
     * Тестирование команды /allrecommendbooks с пустым списком книг.
     */
    @Test
    public void testAllRecommendBooksCommandWithEmptyList() {
        ArrayList<String> emptyList = new ArrayList<>();
        when(storage.getRecBooks()).thenReturn(emptyList);
        String response = messageHandling.parseMessage("/allrecommendbooks", ChatId);
        Assert.assertEquals("Список книг пуст.", response);
    }


    /**
     * Тестирование команды /allrecommendbooks с непустым списком книг.
     */
    @Test
    public void testAllRecommendBooksCommandWithNonEmptyList() {
        ArrayList<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("Book 1");
        nonEmptyList.add("Book 2");
        when(storage.getRecBooks()).thenReturn(nonEmptyList);
        String response = messageHandling.parseMessage("/allrecommendbooks", ChatId);
        Assert.assertEquals("Рекомендованные пользователями книги:\n1. Book 1\n2. Book 2\n", response);
    }


    /**
     * Тестирование команды /searchbyauthor с существующим автором.
     */
    @Test
    public void testSearchBooksByAuthorCommandWithExistAuthor(){
        String author = "John Doe";
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.searchBooksByAuthor(author)).thenReturn(books);
        messageHandling.parseMessage("/searchbyauthor", ChatId);
        String response = messageHandling.parseMessage(author, ChatId);
        Assert.assertEquals("Найденные книги автора 'John Doe':\n- Book 1\n- Book 2\n", response);
    }


    /**
     * Тестирование команды /searchbyauthor с несуществующим автором.
     */
    @Test
    public void testSearchBooksByAuthorCommandWithNoExistAuthor(){
        String author = "John Doe";
        ArrayList<String> books = new ArrayList<>();
        when(storage.searchBooksByAuthor(author)).thenReturn(books);
        messageHandling.parseMessage("/searchbyauthor", ChatId);
        String response = messageHandling.parseMessage(author, ChatId);
        Assert.assertEquals("Книг автора 'John Doe' не найдено.", response);
    }


    /**
     * Тестирование команды /searchbygenre с отсутствием книг по жанру.
     */
    @Test
    public void testSearchBooksByGenreCommandWithNoBooks() {
        String genre = "История";
        when(storage.searchBooksByGenre(genre)).thenReturn(new ArrayList<>());
        messageHandling.parseMessage("/searchbygenre", ChatId);
        String response = messageHandling.parseMessage(genre, ChatId);
        Assert.assertEquals("Книг по указанному жанру не найдено.", response);
    }


    /**
     * Тестирование команды /searchbygenre с некорректным жанром.
     */
    @Test
    public void testSearchBooksByGenreCommandWithWrongGenre() {
        String genre = "Цветы";
        messageHandling.parseMessage("/searchbygenre", ChatId);
        String response = messageHandling.parseMessage(genre, ChatId);
        Assert.assertEquals("Неверный жанр, выберите жанр из списка выше. Если вы уверены, что жанр верный, проверьте точное написание жанра, как в списке выше", response);
    }


    /**
     * Тестирование команды /searchbygenre с наличием книг по жанру.
     */
    @Test
    public void testSearchBooksByGenreCommandWithBooks() {
        String genre = "История";
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.searchBooksByGenre(genre)).thenReturn(books);
        messageHandling.parseMessage("/searchbygenre", ChatId);
        String response = messageHandling.parseMessage(genre, ChatId);
        Assert.assertEquals("Найденные книги по жанру 'История':\n- Book 1\n- Book 2\n", response);
    }


    /**
     * Тестирование команды /removerecbook для удаления книги с корректным номером.
     */
    @Test
    public void testRemoveRecommendBookCommandWithValidBookNumber() {
        long ChatID = 1823368641;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1\nAuthor 1\nИстория\n1823368641");
        readBooks.add("Book 2\nAuthor 2\nДетектив\n1823368640");
        when(storage.getRecBooks()).thenReturn(books);
        when(storage.getAllRecValues()).thenReturn(readBooks);
        messageHandling.parseMessage("/removerecbook", ChatID);
        String response = messageHandling.parseMessage("1", ChatID);
        Assert.assertEquals("Книга Book 1 успешно удалена из списка!", response);
    }


    /**
     * Тестирование команды /removerecbook для удаления книги с некорректным номером.
     */
    @Test
    public void testRemoveRecommendBookCommandWithInvalidBookNumber() {
        long ChatID = 1823368641;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1\nAuthor 1\nИстория\n1823368641");
        readBooks.add("Book 2\nAuthor 2\nДетектив\n1823368640");
        when(storage.getRecBooks()).thenReturn(books);
        when(storage.getAllRecValues()).thenReturn(readBooks);
        messageHandling.parseMessage("/removerecbook", ChatID);
        String response = messageHandling.parseMessage("5", ChatID);
        Assert.assertEquals("Указанный уникальный номер книги не существует в списке книг.", response);
    }

    /**
     * Тестирование команды /removerecbook для удаления книги с нечисловым вводом.
     */
    @Test
    public void testRemoveRecommendBookCommandWithNaN() {
        long ChatID = 1823368641;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1\nAuthor 1\nИстория\n1823368641");
        readBooks.add("Book 2\nAuthor 2\nДетектив\n1823368640");
        when(storage.getRecBooks()).thenReturn(books);
        when(storage.getAllRecValues()).thenReturn(readBooks);
        messageHandling.parseMessage("/removerecbook", ChatID);
        String response = messageHandling.parseMessage("Wrong", ChatID);
        Assert.assertEquals("Некорректный формат номера книги.", response);
    }

    /**
     * Тестирование команды /removerecbook для удаления книги пользователя, не добавившего ее.
     */
    @Test
    public void testRemoveRecommendBookCommandWithNoYourRecommendBooks() {
        long ChatID = 1823368641;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1\nAuthor 1\nИстория\n1823368640");
        readBooks.add("Book 2\nAuthor 2\nДетектив\n1823368640");
        when(storage.getRecBooks()).thenReturn(books);
        when(storage.getAllRecValues()).thenReturn(readBooks);
        messageHandling.parseMessage("/removerecbook", ChatID);
        String response = messageHandling.parseMessage("1", ChatID);
        Assert.assertEquals("Вы не можете удалить книгу, которую добавляли не вы", response);
    }


    /**
     * Тестирование команды /removerecbook для удаления книги из пустого списка.
     */
    @Test
    public void testRemoveRecommendBookCommandWithNoBooks() {
        long ChatID = 1823368641;
        ArrayList<String> books = new ArrayList<>();
        when(storage.getRecBooks()).thenReturn(books);
        String response =  messageHandling.parseMessage("/removerecbook", ChatID);
        Assert.assertEquals("Список книг пуст.", response);
    }
}