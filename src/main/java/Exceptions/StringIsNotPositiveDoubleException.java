package Exceptions;

public class StringIsNotPositiveDoubleException extends InputException {
    @Override
    public String getMessage() {
        return "Данные не являются дробным числом: " + getInvalidInput();
    }

    public StringIsNotPositiveDoubleException(String input) {
        super(input);
    }
}
