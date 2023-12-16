package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Реализация интерфейсов в классе Storage
class Storage implements BookStorage {
    private static Storage instance; // Единственный экземпляр Storage

    private final ArrayList<String> quoteList;
    private Connection readBooksConnection; // Подключение к базе данных read_books.db
    private Connection recommendedBooksConnection; // Подключение к базе данных recommendedBooks.db

    // Приватный конструктор
    Storage() {
        quoteList = new ArrayList<>();
        quoteList.add("Цитата: Начинать всегда стоит с того, что сеет сомнения. \n\nБорис Стругацкий.");
        quoteList.add("Цитата: 80% успеха - это появиться в нужном месте в нужное время.\n\nВуди Аллен");
        quoteList.add("Цитата: Мы должны признать очевидное: понимают лишь те,кто хочет понять.\n\nБернар Вербер");
    }

    // Статический метод для получения единственного экземпляра Storage
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    // Приватный метод для ленивой инициализации подключения
    private Connection getReadBooksConnection() throws SQLException {
        if (readBooksConnection == null || readBooksConnection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                readBooksConnection = DriverManager.getConnection("jdbc:sqlite:D:\\Task5\\Java_OOP\\read_books.db");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return readBooksConnection;
    }

    // Приватный метод для ленивой инициализации подключения к recommendedBooks.db
    private Connection getRecommendedBooksConnection() throws SQLException {
        if (recommendedBooksConnection == null || recommendedBooksConnection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                recommendedBooksConnection = DriverManager.getConnection("jdbc:sqlite:D:\\Task5\\recommendedBooks.db");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return recommendedBooksConnection;
    }

    /**
     * Метод для получения произвольной цитаты из quoteList
     */
    public String getRandQuote() {
        //получаем случайное значение в интервале от 0 до самого большого индекса
        int randValue = (int) (Math.random() * quoteList.size());
        //Из коллекции получаем цитату со случайным индексом и возвращаем ее
        return quoteList.get(randValue);
    }

    /**
     * Метод для получения списка прочитанных книг
     */
    public ArrayList<String> getReadBooks(long chatId) {
        ArrayList<String> books = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getReadBooksConnection().prepareStatement("SELECT title FROM read_books WHERE chat_id = ?");
            statement.setLong(1, chatId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                books.add(resultSet.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return books;
    }


    public Map<String, List<String>> getAllRecBooks() {
        Map<String, List<String>> booksByGenre = new HashMap<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getRecommendedBooksConnection().prepareStatement("SELECT genre, title, author FROM recommendedBooks");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                // Если жанр ещё не добавлен, создаем новую запись
                if (!booksByGenre.containsKey(genre)) {
                    booksByGenre.put(genre, new ArrayList<>());
                }

                // Добавляем информацию о книге в соответствующий жанр
                booksByGenre.get(genre).add(title + " " + author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return booksByGenre;
    }

    public boolean recommendedBookExists(long chatId, String title, String author) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getRecommendedBooksConnection().prepareStatement("SELECT COUNT(*) FROM recommendedBooks WHERE chat_id = ? AND title = ? AND author = ?");
            statement.setLong(1, chatId);
            statement.setString(2, title);
            statement.setString(3, author);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




        /**
         * Метод для добавления книги в список прочитанных книг по формату: название /n автор /n год
         */
        public void addReadBook(String title, String author, int year, long chatId) {
            PreparedStatement statement = null;
            try {
                statement = getReadBooksConnection().prepareStatement("INSERT INTO read_books (title, author, year, chat_id) VALUES (?, ?, ?, ?)");
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setInt(3, year);
                statement.setLong(4, chatId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    /**
     * Метод для добавления книги в список рекомендованных книг
     */
    public void addRecBook(String title, String author, String genre, int year, long chatId) {
        PreparedStatement statement = null;
        try {
            statement = getRecommendedBooksConnection().prepareStatement("INSERT INTO recommendedBooks (title, author, genre, year, chat_id) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, genre);
            statement.setInt(4, year);
            statement.setLong(5, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


        /**
         * Метод для замены книги в списке прочитанных книг по формату: старое_название /n старый_автор /n старый_год новое_название /n новый_автор /n новый_год
         */
        public void editReadBook(String oldTitle, String oldAuthor, int oldYear, String newTitle, String newAuthor, int newYear, long chatId) {
            PreparedStatement statement = null;
            try {
                statement = getReadBooksConnection().prepareStatement("UPDATE read_books SET title = ?, author = ?, year = ? WHERE title = ? AND author = ? AND year = ? AND chat_id = ?");
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
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    /**
     * Метод для полной очистки списка прочитанных книг
     */
    public void clearReadBooks(long chatId) {
        PreparedStatement statement = null;
        try {
            statement = getReadBooksConnection().prepareStatement("DELETE FROM read_books WHERE chat_id = ?");
            statement.setLong(1, chatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

        /**
         * Метод для получения книг одного автора из списка прочитанных книг
         */
        public ArrayList<String> getBooksByAuthor(String author, long chatId) {
            ArrayList<String> books = new ArrayList<>();
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                statement = getReadBooksConnection().prepareStatement("SELECT title FROM read_books WHERE author = ? AND chat_id = ?");
                statement.setString(1, author);
                statement.setLong(2, chatId);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    books.add(resultSet.getString("title"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return books;
        }

    /**
     * Метод для получения книг по конкретному году из списка прочитанных книг
     */
    public ArrayList<String> getBooksByYear(int year, long chatId) {
        ArrayList<String> books = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = getReadBooksConnection().prepareStatement("SELECT title FROM read_books WHERE year = ? AND chat_id = ?");
            statement.setInt(1, year);
            statement.setLong(2, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                books.add(resultSet.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return books;
    }

        /**
         * Метод для проверки существования книги в списке прочитанных книг
         */
        public boolean bookExists(String title, String author, int year, long chatId) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            boolean exists = false;

            try {
                statement = getReadBooksConnection().prepareStatement("SELECT * FROM read_books WHERE title = ? AND author = ? AND year = ? AND chat_id = ?");
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setInt(3, year);
                statement.setLong(4, chatId);

                resultSet = statement.executeQuery();
                exists = resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return exists;
        }

    /**
     * Метод для обновления списка прочитанных книг
     */
    public void updateReadBooks(long chatId, ArrayList<String> readBooks) {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement insertStatement = null;
        PreparedStatement deleteStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getReadBooksConnection();
            ArrayList<String> currentBooks = getReadBooks(chatId);

            for (String book : currentBooks) {
                if (!readBooks.contains(book)) {
                    deleteStatement = connection.prepareStatement("DELETE FROM read_books WHERE chat_id = ? AND title = ?");
                    deleteStatement.setLong(1, chatId);
                    deleteStatement.setString(2, book);
                    deleteStatement.executeUpdate();
                }
            }

            for (String book : readBooks) {
                selectStatement = connection.prepareStatement("SELECT * FROM read_books WHERE chat_id = ? AND title = ?");
                selectStatement.setLong(1, chatId);
                selectStatement.setString(2, book);
                resultSet = selectStatement.executeQuery();

                if (!resultSet.next()) {
                    insertStatement = connection.prepareStatement("INSERT INTO read_books (title, chat_id) VALUES (?, ?)");
                    insertStatement.setString(1, book);
                    insertStatement.setLong(2, chatId);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStatement != null) selectStatement.close();
                if (insertStatement != null) insertStatement.close();
                if (deleteStatement != null) deleteStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для получения списка прочитанных книг в полном формате (название, автор, год)
     */
    public ArrayList<String> getAllValues(long chatId) {
        ArrayList<String> allValues = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = getReadBooksConnection().prepareStatement("SELECT title, author, year FROM read_books WHERE chat_id = ?");
            statement.setLong(1, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int year = resultSet.getInt("year");
                allValues.add(title + "\n" + author + "\n" + year);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return allValues;
    }
}
