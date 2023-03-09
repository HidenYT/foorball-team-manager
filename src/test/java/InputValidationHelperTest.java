import Exceptions.*;
import Helpers.InputValidationHelper;
import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;


public class InputValidationHelperTest {
    @BeforeEach
    public void before() {
        System.out.println("Запуск теста");
    }
    @BeforeAll
    public static void start() {
        System.out.println("Начало тестирования проверки ввода");
    }
    @Test
    public void testvalidateInput() {
        Assert.assertThrows(NameWithNumbersException.class, () -> InputValidationHelper.validateName("1Evgeny"));
        Assert.assertThrows(NameWithNumbersException.class, () -> InputValidationHelper.validateName("Dmitriy1"));
        Assert.assertThrows(NameWithNumbersException.class, () -> InputValidationHelper.validateName("Dmi1triy"));
        Assert.assertThrows(NameWithNumbersException.class, () -> InputValidationHelper.validateName("Dmi123triy"));
        Assert.assertThrows(NameWithNumbersException.class, () -> InputValidationHelper.validateName("1"));
        Assert.assertThrows(StringIsNotTimeException.class, () -> InputValidationHelper.validateTime("a"));
        Assert.assertThrows(StringIsNotTimeException.class, () -> InputValidationHelper.validateTime("aa"));
        Assert.assertThrows(StringIsNotTimeException.class, () -> InputValidationHelper.validateTime("12-01"));
        Assert.assertThrows(StringIsNotTimeException.class, () -> InputValidationHelper.validateTime("1:25"));
        Assert.assertThrows(StringIsNotTimeException.class, () -> InputValidationHelper.validateTime("1:253"));
        Assert.assertThrows(StringIsNotPositiveDoubleException.class,
                () -> InputValidationHelper.validatePositiveDouble("894,848"));
        Assert.assertThrows(StringIsNotPositiveDoubleException.class,
                () -> InputValidationHelper.validatePositiveDouble("a"));
        Assert.assertThrows(StringIsNotPositiveDoubleException.class,
                () -> InputValidationHelper.validatePositiveDouble("a1a"));
        Assert.assertThrows(StringIsNotDateException.class, () -> InputValidationHelper.validateDate("a1a"));
        Assert.assertThrows(StringIsNotDateException.class, () -> InputValidationHelper.validateDate("12-07-2002"));
        Assert.assertThrows(StringIsNotDateException.class, () -> InputValidationHelper.validateDate("321"));
        Assert.assertThrows(StringIsNotPositiveIntException.class,
                () -> InputValidationHelper.validatePositiveInteger("86484,15"));
        Assert.assertThrows(StringIsNotPositiveIntException.class,
                () -> InputValidationHelper.validatePositiveInteger("a"));
        Assert.assertThrows(StringIsNotPositiveIntException.class,
                () -> InputValidationHelper.validatePositiveInteger("aa"));
    }
    @AfterAll
    public static void finish() {
        System.out.println("Конец тестирования проверки ввода");
    }
    @AfterEach
    public void finishedtest() {
        System.out.println("Завершение теста");
    }
}
