package org.example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для работы с коллекцией книг.
 */
public interface BooksInterface {

    /**
     * Инициализирует коллекцию книг.
     *
     * @param allBooks Список всех книг.
     */
    void initializeBooks(List<TheBooks.Book> allBooks);

    /**
     * Получает книгу по её порядковому номеру в коллекции.
     *
     * @param bookNumber Порядковый номер книги.
     * @return Книга по указанному номеру.
     */
    TheBooks.Book getBookByNumber(int bookNumber);

    /**
     * Возвращает количество книг в коллекции.
     *
     * @return Количество книг.
     */
    int getSize();

    /**
     * Получает порядковый номер книги в коллекции.
     *
     * @param book Книга, порядковый номер которой нужно получить.
     * @return Порядковый номер книги.
     */
    int getNumber(TheBooks.Book book);


    /**
     * Получает текущий номер десятки в коллекции книг.
     *
     * @return Текущий номер десятки.
     */
    int getCurrentSet();

    /**
     * Обновляет текущий месяц на основе текущей даты.
     */
    void updateCurrentMonth();

    /**
     * Обновляет текущую десятку книг в соответствии с текущим месяцем.
     */
    void updateCurrentSet();

    /**
     * Устанавливает текущую дату.
     *
     * @param currentDate Новая текущая дата.
     */
    void setCurrentDate(LocalDateTime currentDate);

    /**
     * Интерфейс для представления книги в коллекции.
     */
    interface Book {

        /**
         * Получает название книги.
         *
         * @return Название книги.
         */
        String getTitle();

        /**
         * Получает автора книги.
         *
         * @return Автор книги.
         */
        String getAuthor();

        /**
         * Представляет книгу в виде строки.
         *
         * @return Строковое представление книги.
         */
        String toString();
    }
}

