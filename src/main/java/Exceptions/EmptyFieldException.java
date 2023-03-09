package Exceptions;

public class EmptyFieldException extends Exception{
    private final String fieldName;
    public EmptyFieldException(String field) {
        fieldName = field;
    }

    @Override
    public String getMessage() {
        return "Поле " + fieldName + " не должно быть пустым.";
    }
}
