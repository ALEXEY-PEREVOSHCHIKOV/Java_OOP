package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum BookInputStep {
     TITLE,
    AUTHOR,
    NUMBER, 
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

    private boolean editBookMode;

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
        editBookMode = false;
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
        }else if (editBookMode){
            response = handleEditBookMode(textMsg, chatId);
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


        } else   if (textMsg.equals("/editbook")) {
            // Переходим в режим редактирования книг
            editBookMode = true;
            bookInputSteps.put(chatId, BookInputStep.NUMBER);
            bookData.put(chatId, ""); // Инициализируем пустой строкой
            response = "Введите номер книги из списка /getread, которую хотите изменить:";


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
            String author = textMsg.substring(13); // Получаем имя автора из сообщения
            ArrayList<String> booksByAuthor = storage.getBooksByAuthor(author, chatId);
            if (!booksByAuthor.isEmpty()) {
                response = "Книги автора " + author + ":\n" + String.join("\n", booksByAuthor);
            } else {
                response = "Нет прочитанных книг этого автора.";
            }


        } else if (textMsg.startsWith("/getbyyear")) {
            int year = Integer.parseInt(textMsg.substring(11)); // Получаем год из сообщения
            ArrayList<String> getBooksByYear = storage.getBooksByYear(year, chatId);
            if (!getBooksByYear.isEmpty()) {
                response = "Книги" + " " + year + " " + "года" + ":\n" + String.join("\n", getBooksByYear);
            } else {
                response = "Нет прочитанных книг в этом году.";
            }


        } else if (textMsg.startsWith("/removebook")) {
                   String message = textMsg.substring(12);
                   try {
                       int bookNumber = Integer.parseInt(message);
                       ArrayList<String> readBooks = storage.getReadBooks(chatId);
                       if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                           String removedBook = readBooks.remove(bookNumber - 1); // Удаляем книгу и получаем ее данные
                           // Здесь можно использовать removedBook для получения информации об удаленной книге
                           storage.updateReadBooks(chatId, readBooks); // Обновляем список без удаленной книги
                           response = "Книга " + removedBook + " успешно удалена из списка прочитанных!";
                       } else {
                           response = "Указанный номер книги не существует.";
                       }
                   } catch(NumberFormatException e) {
                         response = "Некорректный формат номера книги";
                   }


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

     // Обработка режима редактирования книг
    private String handleEditBookMode(String textMsg, long chatId) {
        String response;

        // Проверяем текущий шаг ввода для данного чата
        BookInputStep currentStep = bookInputSteps.getOrDefault(chatId, BookInputStep.NUMBER);

        // Проверяем, является ли введенный текст числом
        if (currentStep == BookInputStep.NUMBER) {
            try {
                int bookNumber = Integer.parseInt(textMsg.trim());

                // Проверяем существование книги с указанным уникальным номером в списке прочитанных книг
                ArrayList<String> readBooks = storage.getAllValues(chatId);
                if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                    // Сохраняем номер книги для последующего использования
                    bookData.put(chatId, textMsg.trim());
                    bookInputSteps.put(chatId, BookInputStep.TITLE); // Переходим к следующему шагу
                    response = "Теперь введите новое название книги:";
                } else {
                    response = "Указанный уникальный номер книги не существует в списке прочитанных книг.";
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат номера книги.";
            }
        } else {
            // Если это не число, то предполагаем, что это данные книги
            switch (currentStep) {
                case TITLE:
                    bookData.put(chatId, bookData.get(chatId) + "\n" + textMsg.trim()); // Сохраняем новое название книги
                    bookInputSteps.put(chatId, BookInputStep.AUTHOR); // Переходим к следующему шагу
                    response = "Теперь введите нового автора книги:";
                    break;
                case AUTHOR:
                    bookData.put(chatId, bookData.get(chatId) + "\n" + textMsg.trim()); // Сохраняем автора книги
                    bookInputSteps.put(chatId, BookInputStep.YEAR); // Переходим к следующему шагу
                    response = "Теперь введите новый год прочтения книги:";
                    break;
                case YEAR:
                    try {
                        bookData.put(chatId, bookData.get(chatId) + "\n" + textMsg.trim()); // Сохраняем год прочтения книги
                        String[] parts = bookData.get(chatId).split("\n");
                            int bookNumber = Integer.parseInt(parts[0]);
                            // Получаем новые данные книги
                            String newTitle = parts[1];
                            String newAuthor = parts[2];
                            int newYear = Integer.parseInt(parts[3]);
                        // Получаем старые данные книги
                        ArrayList<String> readBooks = storage.getAllValues(chatId);
                        String[] oldBookParts = readBooks.get(bookNumber - 1).split("\n");
                        String oldTitle = oldBookParts[0];
                        String oldAuthor = oldBookParts[1];
                        int oldYear = Integer.parseInt(oldBookParts[2]);
                                //Обновляем данные о книге в базе данных
                                storage.editReadBook(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);
                                editBookMode = false;
                                response = "Книга '" + oldTitle + "' успешно отредактирована в списке прочитанных!";
                            // Сбрасываем состояние редактирования книги для данного чата
                            bookInputSteps.remove(chatId);
                            bookData.remove(chatId);
                    } catch (NumberFormatException e) {
                        response = "Некорректный формат года прочтения. Пожалуйста, введите год цифрами.";
                        // Переходим к предыдущему шагу
                        bookInputSteps.put(chatId, BookInputStep.AUTHOR);
                    }
                    break;
                default:
                    response = "Неизвестная ошибка в процессе редактирования книги.";
            }
        }
        return response;
    }

}



