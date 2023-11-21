package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum BookInputStep {
    TITLE,
    AUTHOR,
    YEAR
}

/**
 * Класс для обработки сообщений пользователя
 */
public class MessageHandling implements MessageHandlingInterface {

    private Storage storage;

    private PuzzleGame puzzleGame;

    private boolean puzzleMode;

    private boolean bookMode;

    private boolean authorBookMode;

    private boolean yearBookMode;

    private boolean removeBookMode;

    // Добавлены поля для отслеживания состояния добавления книги
    private Map<Long, BookInputStep> bookInputSteps;

    private Map<Long, String> bookData; // Собранные данные о книге


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
        bookMode = false;
        authorBookMode = false;
        yearBookMode = false;
        removeBookMode = false;
        bookInputSteps = new HashMap<>();
        bookData = new HashMap<>();
    }

    /**
     * Метод для обработки входящего текстового сообщения от пользователя.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в виде строки.
     */

    public String parseMessage(String textMsg, long chatId) {
        String response;

        if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        }else if (bookMode){
            response = handleBookMode(textMsg, chatId);
        }else if(authorBookMode){
            response = handleGetByAuthor(textMsg, chatId);
        }else if(yearBookMode){
            response = handleGetByYear(textMsg, chatId);
        }else if(removeBookMode){
            response = handleRemoveBook(textMsg, chatId);
        }else
            response = handleDefaultMode(textMsg, chatId);

        return response;
    }


    /**
     * Обработчик сообщений в режиме головоломки.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме головоломки.
     */
    private String handlePuzzleMode(String textMsg, long chatId) {
        String response;
        if ((textMsg.equalsIgnoreCase("дай подсказку"))||(textMsg.equals("/gethint"))) {
            response = puzzleGame.getHint();
        } else if ((textMsg.equalsIgnoreCase("следующая загадка"))||(textMsg.equals("/anotheriddle"))) {
            response = puzzleGame.getNextPuzzle(chatId);
        } else if (textMsg.equals("/restart")) {
            response = puzzleGame.restart(chatId);
        } else if ((textMsg.equalsIgnoreCase("какой ответ"))||(textMsg.equals("/getanswer"))) {
            response = puzzleGame.getAnswerAndNextPuzzle(chatId);
        } else if (textMsg.equals("/stoppuzzle")) {
            response = "Режим головоломки завершен.\n" + puzzleGame.getStatistics(chatId);;
            puzzleMode = false; // Выход из режима головоломки
        } else {
            response = puzzleGame.checkAnswer(chatId, textMsg);
        }
        return response;
    }


    /**
     * Обработчик сообщений в режиме по умолчанию.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме по умолчанию.
     */
    private String handleDefaultMode(String textMsg, long chatId) {
        String response;
        // Сравниваем текст пользователя с командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("/help")) {
            response = "Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанров книг.";
        } else if (textMsg.equals("/get") || textMsg.equals("Просвети")) {
            response = storage.getRandQuote();
        } else if (textMsg.equals("/genre")) {
            response = "Здравствуйте, добро пожаловать в бот рекомендации книг! Нажмите /chat и выберите жанр";
        } else if (textMsg.equals("Научная фантастика")) {
            response = "Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'";
        } else if (textMsg.equals("Фэнтези")) {
            response = "Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'";
        } else if (textMsg.equals("Романтика")) {
            response = "Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'";
        } else if (textMsg.equals("Детектив")) {
            response = "Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'";


        } else if (textMsg.startsWith("/addbook")) {
            // Переходим к обработке добавления книги
            bookMode = true;
            bookInputSteps.put(chatId, BookInputStep.TITLE);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите название книги:";


        } else if (textMsg.startsWith("/editbook")) {
            // Получаем уникальный номер книги и новые данные книги, введенные пользователем
            String[] parts = textMsg.substring(10).split("\n");
            if (parts.length == 4) {
                int bookNumber;
                String newTitle;
                String newAuthor;
                int newYear;
                try {
                    // Получаем уникальный номер книги
                    bookNumber = Integer.parseInt(parts[0].trim());
                    // Получаем новые данные книги
                    newTitle = parts[1].trim();
                    newAuthor = parts[2].trim();
                    newYear = Integer.parseInt(parts[3].trim());

                    // Проверяем существование книги с указанным уникальным номером в списке прочитанных книг
                    ArrayList<String> readBooks = storage.getAllValues(chatId);
                    if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                        // Получаем старые данные книги
                        String[] oldBookParts = readBooks.get(bookNumber - 1).split("\n");
                        String oldTitle = oldBookParts[0];
                        String oldAuthor = oldBookParts[1];
                        int oldYear = Integer.parseInt(oldBookParts[2]);

                        // Заменяем книгу в базе данных
                        storage.editReadBook(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);
                        response = "Книга '" + oldTitle + "' успешно заменена на книгу '" + newTitle + "' от автора " + newAuthor + " (год: " + newYear + ") в списке прочитанных!";
                    } else {
                        response = "Указанный уникальный номер книги не существует в списке прочитанных книг.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат уникального номера книги или года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /editbook Уникальный_номер\n Новое_название\nНовый_автор\nНовый_год";
            }


        } else if (textMsg.equals("/clearread")) {
            // Очищаем список прочитанных книг
            storage.clearReadBooks(chatId);
            response = "Список прочитанных книг очищен!";


        } else if (textMsg.equals("/getread")) {
            // Получаем список прочитанных книг с уникальными номерами
            ArrayList<String> readBooks = storage.getReadBooks(chatId);
            if (readBooks.isEmpty()) {
                response = "Список прочитанных книг пуст.";
            } else {
                StringBuilder responseBuilder = new StringBuilder("Прочитанные книги:\n");
                for (int i = 0; i < readBooks.size(); i++) {
                    responseBuilder.append(i + 1).append(". ").append(readBooks.get(i)).append("\n");
                }
                response = responseBuilder.toString();
            }


        } else if (textMsg.startsWith("/getbyauthor")) {
            authorBookMode = true;
            bookInputSteps.put(chatId, BookInputStep.TITLE);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите автора книги, которую хотите вывести:";


        } else if (textMsg.startsWith("/getbyyear")) {
            yearBookMode = true;
            bookInputSteps.put(chatId, BookInputStep.TITLE);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите год книги, которую хотите вывести:";


        } else if (textMsg.startsWith("/removebook")) {
            removeBookMode = true;
            bookInputSteps.put(chatId, BookInputStep.TITLE);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите номер книги из списка /getread, которую хотите удалить:";


        } else if (textMsg.equals("/playpuzzle")) {
            // Вход в режим головоломки
            puzzleMode = true;
            response = puzzleGame.startPuzzle(chatId);


        }else {
            response = textMsg;
        }
        return response;
    }


    // Обновленный метод для обработки добавления книги
    private String handleBookMode(String textMsg, long chatId) {
        String response;

        // Проверяем текущий шаг ввода для данного чата
        BookInputStep currentStep = bookInputSteps.getOrDefault(chatId, BookInputStep.TITLE);

        // Если пользователь отправляет произвольное сообщение, предполагаем, что это название книги
        if (currentStep == BookInputStep.TITLE) {
            bookData.put(chatId, textMsg.trim()); // Сохраняем название книги
            bookInputSteps.put(chatId, BookInputStep.AUTHOR); // Переходим к следующему шагу
            response = "Теперь введите автора книги:";
        } else {
            // Иначе обрабатываем ввод в соответствии с текущим шагом
            switch (currentStep) {
                case AUTHOR:
                    bookData.put(chatId, bookData.get(chatId) + "\n" + textMsg.trim()); // Сохраняем автора книги
                    bookInputSteps.put(chatId, BookInputStep.YEAR); // Переходим к следующему шагу
                    response = "Теперь введите год прочтения книги:";
                    break;
                case YEAR:
                    try {
                        int year = Integer.parseInt(textMsg.trim());
                        bookData.put(chatId, bookData.get(chatId) + "\n" + year); // Сохраняем год прочтения книги

                        // Проверяем существование книги в базе данных
                        String[] parts = bookData.get(chatId).split("\n");
                        String title = parts[0].trim();
                        String author = parts[1].trim();

                        if (!storage.bookExists(title, author, year, chatId)) {
                            // Если книги с такими данными нет, добавляем книгу в базу данных
                            storage.addReadBook(title, author, year, chatId);
                            bookMode = false;
                            response = "Книга '" + title + "' от автора " + author + " (год: " + year + ") успешно добавлена в список прочитанных!";
                        } else {
                            bookMode = false;
                            response = "Книга с указанным названием, автором и годом прочтения уже существует в базе данных.";
                        }

                        // Сбрасываем состояние добавления книги для данного чата
                        bookInputSteps.remove(chatId);
                        bookData.remove(chatId);

                    } catch (NumberFormatException e) {
                        response = "Некорректный формат года прочтения. Пожалуйста, введите год цифрами.";
                    }
                    break;
                default:
                    response = "Неизвестная ошибка в процессе добавления книги.";
            }
        }

        return response;
    }

    // Обновленный метод для обработки /getbyauthor
    private String handleGetByAuthor(String textMsg, long chatId) {
        String response;

        // Проверяем текущий шаг ввода для данного чата
        BookInputStep currentStep = bookInputSteps.getOrDefault(chatId, BookInputStep.TITLE);

        // Если пользователь отправляет произвольное сообщение, предполагаем, что это имя автора
        if (currentStep == BookInputStep.TITLE) {
            bookData.put(chatId, textMsg.trim()); // Сохраняем имя автора

            // Получаем список книг по автору из базы данных
            String author = bookData.get(chatId);
            ArrayList<String> booksByAuthor = storage.getBooksByAuthor(author, chatId);

            if (!booksByAuthor.isEmpty()) {
                // Формируем ответ списком книг
                StringBuilder booksResponse = new StringBuilder("Книги автора " + author + ":\n");
                for (String book : booksByAuthor) {
                    booksResponse.append("\"").append(book).append("\";\n");
                }
                response = booksResponse.toString();
                authorBookMode = false;
            } else {
                response = "Нет прочитанных книг этого автора.";
                authorBookMode = false;
            }

            // Сбрасываем состояние для данного чата
            bookInputSteps.remove(chatId);
            bookData.remove(chatId);
            authorBookMode = false;
        } else {
            response = "Неизвестная ошибка в процессе получения книг по автору.";
            authorBookMode = false;
        }

        return response;
    }


    // Обновленный метод для обработки /getbyyear
    private String handleGetByYear(String textMsg, long chatId) {
        String response;

        // Проверяем текущий шаг ввода для данного чата
        BookInputStep currentStep = bookInputSteps.getOrDefault(chatId, BookInputStep.TITLE);

        // Если пользователь отправляет произвольное сообщение, предполагаем, что это год
        if (currentStep == BookInputStep.TITLE) {
            bookData.put(chatId, textMsg.trim()); // Сохраняем год

            // Получаем список книг по году из базы данных
            int year = Integer.parseInt(bookData.get(chatId));
            ArrayList<String> booksByYear = storage.getBooksByYear(year, chatId);

            if (!booksByYear.isEmpty()) {
                // Формируем ответ списком книг
                StringBuilder booksResponse = new StringBuilder("Книги " + year + " года:\n");
                for (String book : booksByYear) {
                    booksResponse.append("\"").append(book).append("\";\n");
                }
                response = booksResponse.toString();
            } else {
                response = "Нет прочитанных книг в этом году.";
                yearBookMode = false;
            }

            // Сбрасываем состояние для данного чата и выключаем флаг
            bookInputSteps.remove(chatId);
            bookData.remove(chatId);
            yearBookMode = false;
        } else {
            response = "Неизвестная ошибка в процессе получения книг по году.";
        }

        return response;
    }

    // Обновленный метод для обработки /removebook
    private String handleRemoveBook(String textMsg, long chatId) {
        String response;

        // Проверяем текущий шаг ввода для данного чата
        BookInputStep currentStep = bookInputSteps.getOrDefault(chatId, BookInputStep.TITLE);

        // Если пользователь отправляет произвольное сообщение, предполагаем, что это номер книги
        if (currentStep == BookInputStep.TITLE) {
            try {
                int bookNumber = Integer.parseInt(textMsg.trim());
                ArrayList<String> readBooks = storage.getReadBooks(chatId);

                if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                    String removedBook = readBooks.remove(bookNumber - 1); // Удаляем книгу и получаем ее данные
                    storage.updateReadBooks(chatId, readBooks); // Обновляем список без удаленной книги
                    response = "Книга " + removedBook + " успешно удалена из списка прочитанных!";
                } else {
                    response = "Указанный номер книги не существует.";
                    removeBookMode = false;
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат номера книги.";
                removeBookMode = false;
            }

            // Сбрасываем состояние для данного чата
            bookInputSteps.remove(chatId);
            bookData.remove(chatId);
            removeBookMode = false;
        } else {
            // Инициализация ввода номера книги для удаления
            bookInputSteps.put(chatId, BookInputStep.TITLE);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите номер книги из списка /getread, которую хотите удалить:";
            removeBookMode = false;
        }

        return response;
    }



}
