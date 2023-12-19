package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс для реализации Телеграмм-бота
 */
public class TelegramBot extends TelegramLongPollingBot implements TelegramBotInterface {

    /**
     * Токен для Telegram-бота.
     * Получено из переменной среды "tgbotToken".
     */
    final private String BOT_TOKEN = System.getenv("tgbotToken");

    /**
     * Имя Telegram-бота.
     * Эта переменная используется для хранения имени бота.
     */
    final private String BOT_NAME = "groobee";

    /**
     * Экземпляр класса MessageHandling.
     * Эта переменная используется для хранения экземпляра обработки сообщения.
     */
    private MessageHandling messageHandling;
    /**
     * Конструктор класса TelegramBot, который инициализирует объекты Storage и MessageHandling.
     * Storage используется для управления базой данных с прочитанными книгами,
     * а MessageHandling - для обработки входящих сообщений от пользователя.
     */
    public TelegramBot() {
        messageHandling = new MessageHandling();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    /**
     * Получение и Отправка сообщения в чат пользователю
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                String userMessage = message.getText();
                long chatId = message.getChatId();

                String response = messageHandling.parseMessage(userMessage, chatId);

                SendMessage outMess = new SendMessage();
                outMess.setChatId(String.valueOf(chatId));
                outMess.setText(response);
                outMess.setReplyMarkup(createKeyboard(chatId)); // Передаем chatId для определения состояния пользователя
                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод для создания клавиатуры в боте
     */
    public ReplyKeyboardMarkup createKeyboard(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        MessageHandling.UserStates userStates = messageHandling.getUserState(chatId);

        if (userStates == MessageHandling.UserStates.PUZZLE_MODE) {
            // Создание клавиатуры для режима головоломки
            KeyboardRow row1 = new KeyboardRow();
            row1.add("/gethint");
            row1.add("/anotheriddle");
            row1.add("/getanswer");

            KeyboardRow row2 = new KeyboardRow();
            row2.add("/restart");
            row2.add("/stoppuzzle");
            row2.add("/back");

            keyboard.add(row1);
            keyboard.add(row2);
        }else {
            // Создание клавиатуры для других режимов
            KeyboardRow row1 = new KeyboardRow();
            row1.add("/start");
            row1.add("/help");
            row1.add("/playpuzzle");

            KeyboardRow row2 = new KeyboardRow();
            row2.add("/vote");
            row2.add("/revote");
            row2.add("/voteresults");


            KeyboardRow row3 = new KeyboardRow();
            row3.add("/getread");
            row3.add("/editbook");
            row3.add("/removebook");
            row3.add("/addbook");



            KeyboardRow row4 = new KeyboardRow();
            row4.add("/recommendbook");
            row4.add("/allrecommendbooks");
            row4.add("/searchbygenre");
            row4.add("/searchbyauthor");
            row4.add("/removerecbook");



            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
            keyboard.add(row4);


        }

        // Установка клавиатуры
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
