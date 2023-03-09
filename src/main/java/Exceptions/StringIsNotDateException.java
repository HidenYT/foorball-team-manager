package Exceptions;

public class StringIsNotDateException extends InputException{
    @Override
    public String getMessage() {
        return "Дата должна иметь формат дд.мм.гггг: " + getInvalidInput();
    }

    public StringIsNotDateException(String input) {
        super(input);
    }
}
