package org.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class PuzzleGameTest implements PuzzleGameTestInterface{
    private long ChatId;

    private MessageHandling bot;

    private PuzzleGame puzzleGame;

    private Map<String, Puzzle> puzzles;


    @Before
    public void setUp() {
        ChatId = 12345L;
        bot = new MessageHandling();
        puzzles = new HashMap<>();
        puzzleGame = new PuzzleGame();
    }

    /**
     * Тестирует метод startPuzzle
     * Проверяет случай, когда все загадки решены.
     */
    @Test
    public void testStartPuzzleGameWhenPuzzlesIsEmpty() {
        puzzleGame.setPuzzles(puzzles);
        Assert.assertEquals("Все загадки решены!", puzzleGame.startPuzzle(ChatId));
    }

    /**
     * Тестирует метод startPuzzle
     * Тестирует случай, когда в puzzles всего одна загадка.
     */
    @Test
    public void testStartPuzzleGameWhenPuzzlesNotEmpty() {
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        puzzleGame.setPuzzles(puzzles);
        Assert.assertEquals("Добро пожаловать в игру в загадки! Начнем.\nЗагадка: Без рук, без ног, а всегда идут", puzzleGame.startPuzzle(ChatId));
    }


    /**
     * Тестирует случай правильного ответа на загадку после обращения к методу startPuzzle и checkAnswer
     */
    @Test
    public void testCorrectAnswerAfterStartPuzzle() {
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        puzzleGame.setPuzzles(puzzles);
        Puzzle currentPuzzle = new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время");
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.checkAnswer(ChatId, "Часы");
        Assert.assertEquals("Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", response);
    }


    /**
     * Тестирует случай неправильного ответа на загадку после обращения к методу startPuzzle и checkAnswer
     */
    @Test
    public void testIncorrectAnswerAfterStartPuzzle() {
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        puzzleGame.setPuzzles(puzzles);
        Puzzle currentPuzzle = new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время");
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.checkAnswer(ChatId, "МУСОР");
        Assert.assertEquals("Неверно! Попробуйте еще раз.", response);
    }


    /**
     * Тестирует метод getHint,
     * Тестирует случай, когда установлена конкретная текущая загадка - currentPuzzle
     */
    @Test
    public void testGetHintWithDefiniteCurrentPuzzle() {
        Puzzle currentPuzzle = new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время");
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.getHint();
        Assert.assertEquals("Подсказка: Показывает время", response);
    }


    /**
     * Тестирует метод getHint,
     * Тестирует случай, когда нет текущей загадки - currentPuzzle
     */
    @Test
    public void testGetHintWithNoCurrentPuzzle() {
        Puzzle currentPuzzle = null;
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.getHint();
        Assert.assertEquals("Нет текущей загадки.", response);
    }


    /**
     * Тестирует метод getNextPuzzle,
     * Тестирует случай правильного ответа на загадку после обращения к методу getNextPuzzle и checkAnswer
     */
    @Test
    public void testAnotheRiddlerWithDefinitiveCurrentPuzzleAndRightAnswer() {
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        Puzzle currentPuzzle = new Puzzle("Имеет ключ, но не открывает замок", "Карта", " Это помогает вам найти путь");
        puzzleGame.setPuzzles(puzzles);
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        Assert.assertEquals("Следующая загадка: Без рук, без ног, а всегда идут", puzzleGame.getNextPuzzle(ChatId));
        Assert.assertEquals("Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", puzzleGame.checkAnswer(ChatId, "Часы"));
    }


    /**
     * Тестирует метод getNextPuzzle,
     * Тестирует случай правильного ответа на загадку после обращения к методу getNextPuzzle и checkAnswer
     */
    @Test
    public void testAnotheRiddlerWithDefinitiveCurrentPuzzleAndWrongAnswer() {
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        Puzzle currentPuzzle = new Puzzle("Имеет ключ, но не открывает замок", "Карта", " Это помогает вам найти путь");
        puzzleGame.setPuzzles(puzzles);
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        Assert.assertEquals("Следующая загадка: Без рук, без ног, а всегда идут", puzzleGame.getNextPuzzle(ChatId));
        Assert.assertEquals("Неверно! Попробуйте еще раз.", puzzleGame.checkAnswer(ChatId, "Мусор"));
    }

    /**
     * Проверяет метод  getNextPuzzle - следующей загадки в загадки для случая, когда загадок нет.
     */
    @Test
    public void testanotheriddleWhenPuzzlesEmpty() {
        Puzzle currentPuzzle = new Puzzle("Имеет ключ, но не открывает замок", "Карта", " Это помогает вам найти путь");
        puzzleGame.setPuzzles(puzzles);
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        Assert.assertEquals("Все загадки решены! Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", puzzleGame.getNextPuzzle(ChatId));
    }

    /**
     * Тестирует метод getAnswerAndNextPuzzle
     * Проверяет случай, когда усановлена конкретная текущая загадка
     */
    @Test
    public void testGetAnswerAndNextPuzzleWithDefinitiveCurrentPuzzle() {
        Puzzle currentPuzzle = new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время");
        puzzleGame.setPuzzles(puzzles);
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.getAnswerAndNextPuzzle(ChatId);
        Assert.assertEquals("Ответ на загадку: Часы.\n" + "Все загадки решены! Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", response);
    }


    /**
     * Тестирует метод getAnswerAndNextPuzzle
     * Проверяет случай, когда текущей загадки нет
     */
    @Test
    public void testGetAnswerAndNextPuzzleWithNoCurrentPuzzle() {
        Puzzle currentPuzzle = null;
        puzzleGame.setCurrentPuzzle(currentPuzzle);
        String response = puzzleGame.getAnswerAndNextPuzzle(ChatId);
        Assert.assertEquals("Нет текущей загадки.", response);
    }

}
