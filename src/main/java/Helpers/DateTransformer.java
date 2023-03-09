package Helpers;

public class DateTransformer {
    public static String normalDateFromDBDate(String date) {
        String[] parts = date.split("-");
        return parts[2] + "." + parts[1] + "." + parts[0];
    }

    public static String DBDateFromNormalDate(String date) {
        String[] parts = date.split("\\.");
        return parts[2] + "-" + parts[1] + "-" + parts[0];
    }
}
