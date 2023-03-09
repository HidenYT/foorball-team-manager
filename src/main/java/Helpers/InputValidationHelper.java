package Helpers;

import Exceptions.*;

import java.util.regex.Pattern;

public class InputValidationHelper {

    public static void validateName(String input) throws NameWithNumbersException {
        if(Pattern.matches(".*\\d.*", input)) throw new NameWithNumbersException(input);
    }

    public static void validatePositiveInteger(String input) throws StringIsNotPositiveIntException {
        if(!Pattern.matches("\\d+", input)) throw new StringIsNotPositiveIntException(input);
    }

    public static void validatePositiveDouble(String input) throws StringIsNotPositiveDoubleException {
        if(!Pattern.matches("\\d+(\\.\\d+)?", input)) throw new StringIsNotPositiveDoubleException(input);
    }

    public static void validateDate(String input) throws StringIsNotDateException {
        if(!Pattern.matches("\\d{2}\\.\\d{2}\\.\\d{4}", input)) throw new StringIsNotDateException(input);
    }

    public static void validateTime(String input) throws StringIsNotTimeException {
        if(!Pattern.matches("\\d{2,3}:\\d{2}", input)) throw new StringIsNotTimeException(input);
    }
}
