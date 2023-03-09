package Helpers;

public class ColumnIndexFinder {
    public static int find(String[] columns, String column) {
        for(int i = 0; i < columns.length; i++) {
            if(column.equals(columns[i])) return i;
        }
        return -1;
    }
}
