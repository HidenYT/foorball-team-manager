package Exceptions;

public class NameWithNumbersException extends InputException{
    @Override
    public String getMessage() {
        return "Имя не может содержать цифры: " + getInvalidInput();
    }

    public NameWithNumbersException(String input) {
        super(input);
    }
}
