package org.example;
import org.junit.Test;


/**
 * Интерфейс для тестирования функционала бота.
 */
public interface PuzzleGameTestInterface {
    /**
     * Подготавливает необходимые ресурсы перед выполнением тестов.
     */
    void setUp();


    /**
     * Тестирует метод startPuzzle
     * Проверяет случай, когда все загадки решены.
     */
    @Test
    void testStartPuzzleGameWhenPuzzlesIsEmpty();


    /**
     * Тестирует метод startPuzzle
     * Тестирует случай, когда в puzzles всего одна загадка.
     */
    @Test
    void testStartPuzzleGameWhenPuzzlesNotEmpty();


    /**
     * Тестирует случай правильного ответа на загадку после обращения к методу startPuzzle и checkAnswer
     */
    @Test
    void testCorrectAnswerAfterStartPuzzle();


    /**
     * Тестирует случай неправильного ответа на загадку после обращения к методу startPuzzle и checkAnswer
     */
    @Test
    void testIncorrectAnswerAfterStartPuzzle();


    /**
     * Тестирует метод getHint,
     * Тестирует случай, когда установлена конкретная текущая загадка - currentPuzzle
     */
    @Test
    void testGetHintWithDefiniteCurrentPuzzle();


    /**
     * Тестирует метод getHint,
     * Тестирует случай, когда нет текущей загадки - currentPuzzle
     */
    @Test
    void testGetHintWithNoCurrentPuzzle();


    /**
     * Тестирует метод getNextPuzzle,
     * Тестирует случай правильного ответа на загадку после обращения к методу getNextPuzzle и checkAnswer
     */
    @Test
    void testAnotheRiddlerWithDefinitiveCurrentPuzzleAndRightAnswer();


    /**
     * Тестирует метод getNextPuzzle,
     * Тестирует случай правильного ответа на загадку после обращения к методу getNextPuzzle и checkAnswer
     */
    @Test
    void testAnotheRiddlerWithDefinitiveCurrentPuzzleAndWrongAnswer();


    /**
     * Проверяет метод  getNextPuzzle - следующей загадки в загадки для случая, когда загадок нет.
     */
    @Test
    void testanotheriddleWhenPuzzlesEmpty();


    /**
     * Тестирует метод getAnswerAndNextPuzzle
     * Проверяет случай, когда усановлена конкретная текущая загадка
     */
    @Test
    void testGetAnswerAndNextPuzzleWithDefinitiveCurrentPuzzle();


    /**
     * Тестирует метод getAnswerAndNextPuzzle
     * Проверяет случай, когда текущей загадки нет
     */
    @Test
    void testGetAnswerAndNextPuzzleWithNoCurrentPuzzle();
}
