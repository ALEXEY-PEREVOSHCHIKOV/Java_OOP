package org.example;

import java.util.ArrayList;

/**
 * Интерфейс для работы с книгами.
 * Позволяет управлять списком прочитанных книг и осуществлять поиск по различным критериям.
 */
public interface BookStorage {
    /**
     * Получает список прочитанных книг для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг в формате строки
     */
    ArrayList<String> getReadBooks(long chatId);

    /**
     * Добавляет книгу в список прочитанных книг.
     *
     * @param title  название книги
     * @param author автор книги
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     */
    void addReadBook(String title, String author, int year, long chatId);

    /**
     * Удаляет все прочитанные книги для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     */
    void clearReadBooks(long chatId);

    /**
     * Получает список прочитанных книг определенного автора для указанного чата.
     *
     * @param author автор книги
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг указанного автора в формате строки
     */
    ArrayList<String> getBooksByAuthor(String author, long chatId);

    /**
     * Получает список прочитанных книг за определенный год для указанного чата.
     *
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг за указанный год в формате строки
     */
    ArrayList<String> getBooksByYear(int year, long chatId);

    /**
     * Изменяет существующую книгу новой книгой в списке прочитанных книг.
     *
     * @param oldTitle  старое название книги
     * @param oldAuthor старый автор книги
     * @param oldYear   старый год прочтения
     * @param newTitle  новое название книги
     * @param newAuthor новый автор книги
     * @param newYear   новый год прочтения
     * @param chatId    уникальный идентификатор чата пользователя
     */
    void editReadBook(String oldTitle, String oldAuthor, int oldYear,
                      String newTitle, String newAuthor, int newYear, long chatId);

    /**
     * Проверяет существование указанной книги в списке прочитанных книг.
     *
     * @param title  название книги
     * @param author автор книги
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return true, если книга существует в списке прочитанных книг, в противном случае - false
     */
    boolean bookExists(String title, String author, int year, long chatId);

    /**
     * Получает случайную цитату.
     *
     * @return случайная цитата в формате строки
     */
    String getRandQuote();
}
