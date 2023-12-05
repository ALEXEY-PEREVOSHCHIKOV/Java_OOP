package org.example;
import java.time.LocalDate;
import java.util.*;

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

    /**
     * Хранилище данных, необходимых для бота.
     */
    private Storage storage;

    /**
     * Игра в головоломку.
     */
    private PuzzleGame puzzleGame;

    /**
     * Модуль голосования за книги.
     */
    private BookVoting bookVoting;

    /**
     * Флаг, указывающий на наличие активного голосования.
     */
    private boolean votingInProgress = false;

    /**
     * День, когда заканчивается голосование.
     */
    private int VOTING_END_DAY = 5;

    /**
     * Множество чатов, в которых в данный момент идет голосование.
     */
    private Set<Long> activeVotingChats = new HashSet<>();

    /**
     * Проверяет, идет ли в данный момент голосование в указанном чате.
     *
     * @param chatId Идентификатор чата.
     * @return {@code true}, если голосование идет, иначе {@code false}.
     */
    private boolean votingInProgressForChat(long chatId) {
        return activeVotingChats.contains(chatId);
    }

    /**
     * Добавляет чат в список активных голосований.
     *
     * @param chatId Идентификатор чата.
     */
    private void addVotingInProgressChat(long chatId) {
        activeVotingChats.add(chatId);
    }


    /**
     * Работа обработчика сообщений в режиме игры в загадки
     */
    private boolean puzzleMode;


    /**
     * Работа обработчика сообщений в режиме добавления книг
     */
    private boolean bookMode;

    /**
     * Работа обработчика сообщений в режиме вывода книг по указанному автору
     */
    private boolean authorBookMode;


    /**
     * Работа обработчика сообщений в режиме вывода книг по указанному году
     */
    private boolean yearBookMode;


    /**
     * Работа обработчика сообщений в режиме удаления книги
     */
    private boolean removeBookMode;

    /**
     * Работа обработчика сообщений в режиме редактирования книги
     */
    private boolean editBookMode;

    /**
     * Работа обработчика сообщений в режиме голосования
     */
    private boolean voteMode;


    /**
     * Отслеживание текущего шага работы с книгой
     */
    private Map<Long, BookInputStep> bookInputSteps;


    /**
     * Собранные данные о книге
     */
    private Map<Long, String> bookData;


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        bookVoting = new BookVoting();
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
        bookMode = false;
        authorBookMode = false;
        yearBookMode = false;
        removeBookMode = false;
        editBookMode = false;
        voteMode = false;

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
        }else if(removeBookMode) {
            response = handleRemoveBook(textMsg, chatId);
        }else if (editBookMode){
            response = handleEditBookMode(textMsg, chatId);
        }else if (voteMode){
            response = handleVoteMode(textMsg, chatId);
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
        }else {
            response = puzzleGame.checkAnswer(chatId, textMsg);
        }
        return response;
    }


    /**
     * Обрабатывает ввод пользователя в режиме голосования
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение в ответ на ввод пользователя.
     */
    private String handleVoteMode(String textMsg, long chatId) {
        String response;
        if (voteMode) {
                response = bookVoting.processUserVotes(textMsg, chatId);
                if (response.equals("Спасибо за ваш голос!")) {
                    // Голосование завершено, устанавливаем voteMode в false
                    voteMode = false;
                }
        } else {
            response = "Книги:";
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
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
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


        } else if (textMsg.equals("/editbook")) {
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

        } else if (textMsg.equals("/vote")) {
            if (currentDay <= VOTING_END_DAY) {
                // Проверяем, не проводится ли уже голосование для данного пользователя
                if (!votingInProgress || !votingInProgressForChat(chatId)) {
                    // Если голосование ещё не начато для данного пользователя, устанавливаем флаг и отображаем список книг
                    votingInProgress = true;
                    voteMode = true;
                    addVotingInProgressChat(chatId); // Добавляем текущий чат в список активных голосований
                    response = " Здравствуйте, добро пожаловать на ежемесячное голосование за “книгу месяца”, которое проводится с 1 по 5 число. Вам предлагается на выбор 10 книг.\n"+bookVoting.showBookList(chatId);
                } else {
                    // Если голосование уже начато для данного пользователя, предлагаем вариант переголосования
                    response = "Если вы пытаетесь проголосовать повторно, то этого сделать нельзя. Если вы хотите переголосовать, нажмите /revote";
                }
            } else {
                if (votingInProgress)
                response = bookVoting.finishVoting();
                else{
                response = "Лидера голосования нет";
                }
            }

        } else if (textMsg.equals("/revote")) {
            if (currentDay <= VOTING_END_DAY) {
                if (votingInProgressForChat(chatId)) {
                    voteMode = true;
                    bookVoting.cancelUserVotes(chatId);
                    response = bookVoting.showBookList(chatId);
                } else {
                    // Если голосование ещё не начато для данного пользователя, предлагаем вариант голосования
                    response = "Вы не можете использовать эту команду до использования /vote";
                }
            } else {
                if (votingInProgress)
                    response = bookVoting.finishVoting();
                else{
                    response = "Лидера голосования нет";
                }
            }

        }else if (textMsg.equals("/voteresults")) {
            if (currentDay <= VOTING_END_DAY) {
                response = bookVoting.getVotingStatistics();
            } else {
                if (votingInProgress)
                    response = bookVoting.finishVoting();
                else {
                    response = "Лидера голосования нет";
                }
            }

        } else {
            response = textMsg;
        }
        return response;
    }


    /**
     * Обрабатывает ввод пользователя в режиме добавления новой книги.
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение в ответ на ввод пользователя.
     */
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


    /**
     * Обрабатывает ввод пользователя для получения списка книг по автору.
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение с результатами запроса книг по автору.
     */
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


    /**
     * Обрабатывает ввод пользователя для получения списка книг по году.
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение с результатами запроса книг по году.
     */
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


    /**
     * Обрабатывает ввод пользователя для удаления книги из списка прочитанных.
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение с результатами удаления книги.
     */
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
                    removeBookMode = false;
                    response = "Книга " + removedBook + " успешно удалена из списка прочитанных!";
                } else {
                    response = "Указанный уникальный номер книги не существует в списке прочитанных книг.";
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат номера книги.";
            }
            // Сбрасываем состояние для данного чата
            bookInputSteps.remove(chatId);
            bookData.remove(chatId);
        } else {
            response = "Неизвестная ошибка в процессе удаления книги.";
        }

        return response;
    }


    /**
     * Обрабатывает ввод пользователя в режиме редактирования существующей книги.
     *
     * @param textMsg Введенное пользователем сообщение.
     * @param chatId  Идентификатор чата пользователя.
     * @return Сообщение в ответ на ввод пользователя.
     */
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
                    }
                    break;
                default:
                    response = "Неизвестная ошибка в процессе редактирования книги.";
            }
        }
        return response;
    }
    
    /**
     * устанавливает день окончания голосования
     */
    public void setVotingEndDay(int votingEndDay) {
        this.VOTING_END_DAY = votingEndDay;
    }
}
