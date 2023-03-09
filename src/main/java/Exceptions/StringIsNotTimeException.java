package Exceptions;

public class StringIsNotTimeException extends InputException{
    public StringIsNotTimeException(String input) {
        super(input);
    }

    @Override
    public String getMessage() {
        return "Данные должны быть в формате мм:сс или ммм:сс:" + getInvalidInput();
    }
}
