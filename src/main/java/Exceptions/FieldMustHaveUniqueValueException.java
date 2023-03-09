package Exceptions;

public class FieldMustHaveUniqueValueException extends Exception {
    private final String fieldName;
    private final String value;

    public FieldMustHaveUniqueValueException(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Значение " + value + " поля " + fieldName + " должно быть уникальным";
    }
}
