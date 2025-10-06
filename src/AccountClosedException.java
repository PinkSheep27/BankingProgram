public class AccountClosedException extends Exception{
    public AccountClosedException(int acctNum,double transacAmnt,String transacType){
        super("Error: " + transacType + " of " + transacAmnt +" not processed due to Account #" + acctNum + " being closed");
    }
    public AccountClosedException(String str){
        super(str);
    }
}
