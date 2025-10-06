public class InsufficientFundsException extends Exception{
    public InsufficientFundsException(double transactionAmount){
        super(String.format("Error: $%.2f Is Over The Balance And Can't be Withdrawn.", transactionAmount));
    }
    public InsufficientFundsException(String str){
        super(str);
    }
}