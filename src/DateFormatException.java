// Exception class used for incorrect input date format
// including YYYYMM format, and date < 1 or date > month.length
public class DateFormatException extends Exception{
    public DateFormatException(){}
}
