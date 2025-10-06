public class InvalidAccountException extends Exception{
    public InvalidAccountException (int acctNum){
        super("Error: Account #" + acctNum + " DNE.");
    }
}