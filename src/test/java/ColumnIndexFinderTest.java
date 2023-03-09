import Helpers.ColumnIndexFinder;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

public class ColumnIndexFinderTest {
    private final String[] testArray1 = {"Cat", "Dog", "Mother", "Father", "Me"};
    @BeforeEach
    public void before() {
        System.out.println("Запуск теста");
    }
    @BeforeAll
    public static void start() {
        System.out.println("Начало тестирования поиска индекса в массиве");
    }
    @Test
    public void testgetColumnIndex() {
        Assert.assertEquals(-1, ColumnIndexFinder.find(testArray1, ""));
        Assert.assertEquals(-1, ColumnIndexFinder.find(testArray1, "123"));
        Assert.assertEquals(-1, ColumnIndexFinder.find(testArray1, "hi"));
        Assert.assertEquals(0, ColumnIndexFinder.find(testArray1, "Cat"));
        Assert.assertEquals(1, ColumnIndexFinder.find(testArray1, "Dog"));
        Assert.assertEquals(2, ColumnIndexFinder.find(testArray1, "Mother"));
        Assert.assertEquals(3, ColumnIndexFinder.find(testArray1, "Father"));
        Assert.assertEquals(4, ColumnIndexFinder.find(testArray1, "Me"));
    }
    @AfterAll
    public static void finish() {
        System.out.println("Конец тестирования поиска индекса в массиве");
    }
    @AfterEach
    public void finishedtest() {
        System.out.println("Завершение теста");
    }
}
