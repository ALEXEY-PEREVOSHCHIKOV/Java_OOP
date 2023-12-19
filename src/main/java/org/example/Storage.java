package org.example;
import java.sql.*;
import java.util.ArrayList;

/**
 * Класс для управления хранилищем книг и цитат.
 */
public class Storage implements BookStorage {


    /**
     * URL базы данных SQLite.
     */
    private static final String DATABASE_URL = "jdbc:sqlite:read_books.db";


    /**
     * Соединение с базой данных.
     */
    private Connection connection;


    /**
     * Список цитат.
     */
    final private ArrayList<String> quoteList;


    /**
     * Конструктор класса. Инициализирует соединение с базой данных и список цитат.
     */
    public Storage() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        quoteList = new ArrayList<>();
        quoteList.add("Цитата: Начинать всегда стоит с того, что сеет сомнения. \n\nБорис Стругацкий.");
        quoteList.add("Цитата: 80% успеха - это появиться в нужном месте в нужное время.\n\nВуди Аллен");
        quoteList.add("Цитата: Мы должны признать очевидное: понимают лишь те,кто хочет понять.\n\nБернар Вербер");
    }

    /**
     * Метод для получения произвольной цитаты из quoteList
     */
    public String getRandQuote()
    {
        //получаем случайное значение в интервале от 0 до самого большого индекса
        int randValue = (int)(Math.random() * quoteList.size());
        //Из коллекции получаем цитату со случайным индексом и возвращаем ее
        return quoteList.get(randValue);
    }

    /**
     * Метод для получения списка прочитанных книг
     */
    public ArrayList<String> getReadBooks(long chatId) {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title FROM read_books WHERE chat_id = ?")) {
            statement.setLong(1, chatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(resultSet.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Метод для добавления книги в список прочитанных книг по формату: название /n автор /n год
     */
    public void addReadBook(String title, String author, int year, long chatId) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO read_books (title, author, year, chat_id) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setLong(4, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для замены книги в списке прочитанных книг по формату: старое_название /n старый_автор /n старый_год новое_название /n новый_автор /n новый_год
     */
    public void editReadBook(String oldTitle, String oldAuthor, int oldYear, String newTitle, String newAuthor, int newYear, long chatId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE read_books SET title = ?, author = ?, year = ? WHERE title = ? AND author = ? AND year = ? AND chat_id = ?")) {
            statement.setString(1, newTitle);
            statement.setString(2, newAuthor);
            statement.setInt(3, newYear);
            statement.setString(4, oldTitle);
            statement.setString(5, oldAuthor);
            statement.setInt(6, oldYear);
            statement.setLong(7, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для полной очистки списка прочитанных книг
     */
    public void clearReadBooks(long chatId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM read_books WHERE chat_id = ?")) {
            statement.setLong(1, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для получения книг одного автора из списка прочитанных книг
     */
    public ArrayList<String> getBooksByAuthor(String author, long chatId) {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title FROM read_books WHERE author = ? AND chat_id = ?")) {
            statement.setString(1, author);
            statement.setLong(2, chatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(resultSet.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    /**
     * Метод для получения книг по конкретному году из списка прочитанных книг
     */
    public ArrayList<String> getBooksByYear(int year, long chatId) {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title FROM read_books WHERE year = ? AND chat_id = ?")) {
            statement.setInt(1, year);
            statement.setLong(2, chatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(resultSet.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    /**
     * Метод для проверки существования книги в списке прочитанных книг
     */
    public boolean bookExists(String title, String author, int year, long chatId) {
        boolean exists = false;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM read_books WHERE title = ? AND author = ? AND year = ? AND chat_id = ?")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setLong(4, chatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                exists = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }


    /**
     * Метод для обновления списка прочитанных книг
     */
    public void updateReadBooks(long chatId, String oldTitle, String oldAuthor, int oldYear) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM read_books WHERE chat_id = ? AND title = ? AND author = ? AND year = ?")) {
            statement.setLong(1, chatId);
            statement.setString(2, oldTitle);
            statement.setString(3, oldAuthor);
            statement.setInt(4, oldYear);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для получения списка прочитанных книг в полном формате (название, автор, год)
     */
    public ArrayList<String> getAllValues(long chatId) {
        ArrayList<String> allValues = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title, author, year FROM read_books WHERE chat_id = ?")) {
            statement.setLong(1, chatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    int year = resultSet.getInt("year");
                    allValues.add(title + "\n" + author + "\n" + year);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }


    /**
     * Метод для добавления рекомендованной книги в базу данных.
     *
     * @param title   Название книги.
     * @param author  Автор книги.
     * @param genre   Жанр книги.
     * @param chatId  Идентификатор чата.
     */
    public void addRecBook(String title, String author, String genre, long chatId) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO recommendedBooks (title, author, genre, chat_id) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, genre);
            statement.setLong(4, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для получения списка названий рекомендованных книг.
     *
     * @return Список названий книг.
     */
    public ArrayList<String> getRecBooks() {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title FROM recommendedBooks")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(resultSet.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }



    /**
     * Метод для проверки существования рекомендованной книги.
     *
     * @param title  Название книги.
     * @param author Автор книги.
     * @return {@code true}, если книга существует, иначе {@code false}.
     */
    public boolean recBookExists(String title, String author) {
        boolean exists = false;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM recommendedBooks WHERE title = ? AND author = ?")) {
            statement.setString(1, title);
            statement.setString(2, author);
            try (ResultSet resultSet = statement.executeQuery()) {
                exists = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }


    /**
     * Метод для поиска книг по указанному жанру.
     *
     * @param genre Жанр книг.
     * @return Список найденных книг в формате "название от автора".
     */
    public ArrayList<String> searchBooksByGenre(String genre) {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title, author FROM recommendedBooks WHERE genre = ?")) {
            statement.setString(1, genre);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    books.add(title + " от автора " + author);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    /**
     * Метод для поиска книг по указанному автору.
     *
     * @param author Автор книг.
     * @return Список найденных книг в формате "название (жанр)".
     */
    public ArrayList<String> searchBooksByAuthor(String author) {
        ArrayList<String> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title, genre FROM recommendedBooks WHERE author = ?")) {
            statement.setString(1, author);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String genre = resultSet.getString("genre");
                    books.add(title + " (жанр: " + genre + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    /**
     * Метод для обновления данных о рекомендованных книгах.
     *
     * @param chatId   Идентификатор чата.
     * @param oldTitle Старое название книги.
     * @param oldAuthor Старый автор книги.
     * @param oldGenre  Старый жанр книги.
     */
    public void updateRecBooks(long chatId, String oldTitle, String oldAuthor, String oldGenre) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM recommendedBooks WHERE chat_id = ? AND title = ? AND author = ? AND genre = ?")) {
            statement.setLong(1, chatId);
            statement.setString(2, oldTitle);
            statement.setString(3, oldAuthor);
            statement.setString(4, oldGenre);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для получения всех значений о рекомендованных книгах.
     *
     * @return Список строк с данными о книгах.
     */
    public ArrayList<String> getAllRecValues() {
        ArrayList<String> allValues = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT title, author, genre, chat_id FROM recommendedBooks")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String genre = resultSet.getString("genre");
                    long chat_id = resultSet.getLong("chat_id");
                    allValues.add(title + "\n" + author + "\n" + genre +"\n" + chat_id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allValues;
    }


    /**
     * Метод для закрытия соединения с базой данных.
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}