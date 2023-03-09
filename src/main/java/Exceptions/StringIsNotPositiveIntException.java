package Exceptions;

public class StringIsNotPositiveIntException extends InputException{
    @Override
    public String getMessage() {
        return "Данные не являются числом: " + getInvalidInput();
    }

    public StringIsNotPositiveIntException(String input) {
        super(input);
    }
}