package Exceptions;

public class InputException extends Exception {
    private final String invalidInput;
    public InputException(String input) {
        invalidInput = input;
    }
    public String getInvalidInput() {
        return invalidInput;
    }
}
