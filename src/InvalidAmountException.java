public class InvalidAmountException extends Exception{
    public InvalidAmountException(double transactionAmount,int acctNum){
        super(String.format("Account Number: %s\nError: $%.2f Is An Invalid Amount", acctNum, transactionAmount));
    }
    public InvalidAmountException(String str){
        super(str);
    }
}