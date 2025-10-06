public class CheckTooOldException extends Exception{
    public CheckTooOldException() {
        super("Error: The Date of the Check is Old.");
    }
}